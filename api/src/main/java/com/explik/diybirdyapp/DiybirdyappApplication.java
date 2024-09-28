package com.explik.diybirdyapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import picocli.CommandLine;

import java.util.Map;

@SpringBootApplication
public class DiybirdyappApplication {
	// Regular startup
	public static void main(String[] args) {
		SpringApplication.run(DiybirdyappApplication.class, args);
	}

	// Command-line startup
	@Bean
	public CommandLineRunner dispatcher(ApplicationContext context) {
		return args -> {
			// Check for command name and arguments
			if (args.length == 0)
				return;

			// Extract the command name and arguments
			String commandName = args[0];
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);

			// Run command if found and shutdown
			Map<String, Object> runners = context.getBeansWithAnnotation(CommandLine.Command.class);

			for (Object runner : runners.values()) {
				CommandLine.Command commandAnnotation = runner.getClass().getAnnotation(CommandLine.Command.class);

				if (!(runner instanceof Runnable))
					continue;
				if (!commandAnnotation.name().equals(commandName))
					continue;

				new CommandLine(runner).execute(newArgs);
				System.exit(0);
				return;
			}

			System.out.println("Command " + commandName + " not found.");
			System.exit(0);
		};
	}
}
