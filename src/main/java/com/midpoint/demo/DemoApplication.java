package com.midpoint.demo;

import com.midpoint.demo.cli.MidPointCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner, ExitCodeGenerator {

	private final IFactory factory;
	private final MidPointCommand midPointCommand;
	private int exitCode;

	public DemoApplication(IFactory factory, MidPointCommand midPointCommand) {
		this.factory = factory;
		this.midPointCommand = midPointCommand;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) {
		CommandLine cmd = new CommandLine(midPointCommand, factory);

		if (args.length > 0) {
			// single command mode
			exitCode = cmd.execute(args);
			return;
		}

		// interactive mode
		try (var scanner = new java.util.Scanner(System.in)) {
			while (true) {
				System.out.print("midpoint> ");
				String line = scanner.nextLine();

				if (line == null || line.trim().equalsIgnoreCase("exit")) {
					break;
				}

				String[] inputArgs = line.trim().split("\\s+");
				cmd.execute(inputArgs);
			}
		}
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}
}
