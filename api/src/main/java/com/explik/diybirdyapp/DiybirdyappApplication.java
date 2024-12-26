package com.explik.diybirdyapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import picocli.CommandLine;

import java.util.Map;

@SpringBootApplication
public class DiybirdyappApplication {
	private static final Logger logger = LoggerFactory.getLogger(DiybirdyappApplication.class);

	// Regular startup
	public static void main(String[] args) {
		logger.debug("Application starting.");
		SpringApplication.run(DiybirdyappApplication.class, args);
		logger.debug("Application started.");
	}

	// Command-line startup
	@Bean
	public CommandLineRunner dispatcher(ApplicationContext context) {
		return args -> {
			// Check for command name and arguments
			if (args.length == 0) {
				logger.debug("No command provided.");
				return;
			}

			// Extract the command name and arguments
			String commandName = args[0];
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            logger.debug("Command {} with {} argument(s) provided.", commandName, newArgs.length);

			// Run command if found and shutdown
			Map<String, Object> runners = context.getBeansWithAnnotation(CommandLine.Command.class);

			for (Object runner : runners.values()) {
				CommandLine.Command commandAnnotation = runner.getClass().getAnnotation(CommandLine.Command.class);

				if (!(runner instanceof Runnable))
					continue;
				if (!commandAnnotation.name().equals(commandName))
					continue;

				logger.debug("Execution of of command {} started.", commandName);
				new CommandLine(runner).execute(newArgs);
				logger.debug("Execution of command {} completed.", commandName);

				System.exit(0);
				return;
			}

            logger.debug("Command {} not found.", commandName);
			System.exit(0);
		};
	}
}
