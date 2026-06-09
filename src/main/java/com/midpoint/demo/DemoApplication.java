package com.midpoint.demo;

import com.midpoint.demo.cli.MidPointCommand;
import com.midpoint.demo.security.LoginManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import java.util.Scanner;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner, ExitCodeGenerator {

	private static final String PROMPT = "midpoint> ";
	private static final String EXIT_COMMAND = "exit";

	private final IFactory factory;
	private final MidPointCommand midPointCommand;
	private final LoginManager loginManager;
	private int exitCode;

	public DemoApplication(IFactory factory, MidPointCommand midPointCommand, LoginManager loginManager) {
		this.factory = factory;
		this.midPointCommand = midPointCommand;
		this.loginManager = loginManager;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) {
		CommandLine cmd = new CommandLine(midPointCommand, factory);

		if (args.length > 0) {
			exitCode = cmd.execute(args);
			return;
		}

		try (var scanner = new Scanner(System.in)) {
			if (loginManager.login(scanner)) {
				startShell(scanner, cmd);
			} else {
				exitCode = 1;
			}
		}
	}

	private void startShell(Scanner scanner, CommandLine cmd) {
		while (true) {
			System.out.print(PROMPT);
			if (!scanner.hasNextLine()) break;
			String line = scanner.nextLine();

			if (isExitCommand(line)) {
				System.out.println("Bye!");
				break;
			}

			if (line.trim().isEmpty()) continue;

			String[] inputArgs = line.trim().split("\\s+");
			cmd.execute(inputArgs);
		}
	}

	private boolean isExitCommand(String line) {
		return line == null || line.trim().equalsIgnoreCase(EXIT_COMMAND);
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}
}
