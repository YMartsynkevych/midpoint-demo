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
			// LOGIN PHASE
			boolean authenticated = false;
			for (int attempt = 1; attempt <= 3; attempt++) {
				System.out.print("Username: ");
				String username = scanner.nextLine();
				System.out.print("Password: ");
				// Use System.console() for sensitive input if available, otherwise fallback to scanner
				String password;
				if (System.console() != null) {
					char[] passwordChars = System.console().readPassword();
					password = new String(passwordChars);
				} else {
					password = scanner.nextLine();
				}

				midPointCommand.getMidPointClient().authenticate(username, password);
				try {
					if (midPointCommand.getMidPointClient().testAuthentication()) {
						System.out.println("Login successful");
						authenticated = true;
						break;
					} else {
						System.out.println("Invalid credentials");
					}
				} catch (Exception e) {
					System.out.println("Invalid credentials");
				}
			}

			if (!authenticated) {
				System.out.println("Too many failed attempts. Terminating.");
				exitCode = 1;
				return;
			}

			// COMMAND MODE
			while (true) {
				System.out.print("midpoint> ");
				if (!scanner.hasNextLine()) break;
				String line = scanner.nextLine();

				if (line == null || line.trim().equalsIgnoreCase("exit")) {
					System.out.println("Bye!");
					break;
				}

				if (line.trim().isEmpty()) continue;

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
