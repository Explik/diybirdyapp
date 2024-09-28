package com.explik.diybirdyapp.command;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Debugging command to test the command-line interface.
 * Run with `java -jar diybirdyapp.jar hello-world`.
 * OR `java -jar diybirdyapp.jar hello-world --name=John`.
 */
@Component
@Command(name = "hello-world", description = "Hello World command")
public class HelloWorldCommand implements Runnable {

    @Option(names = {"-n", "--name"}, description = "Name to greet", defaultValue = "World")
    private String name;

    @Override
    public void run() {
        System.out.printf("Hello %s%n", name);
    }
}