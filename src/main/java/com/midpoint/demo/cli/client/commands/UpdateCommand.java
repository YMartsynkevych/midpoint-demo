package com.midpoint.demo.cli.client.commands;

import com.midpoint.demo.service.UserService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Component
@Command(name = "update", description = "Update user fields by username")
public class UpdateCommand implements Callable<Integer> {
    private final UserService userService;

    @Option(names = {"--username"}, required = true, description = "Username of the user to update")
    private String username;

    @Option(names = {"--email"}, description = "New email address")
    private String email;

    @Option(names = {"--givenName"}, description = "New given name")
    private String givenName;

    @Option(names = {"--familyName"}, description = "New family name")
    private String familyName;

    @Option(names = {"--newName"}, description = "New username")
    private String newName;

    public UpdateCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Integer call() {
        try {
            userService.updateUserByUsername(username, email, givenName, familyName, newName);
            System.out.println("User " + username + " updated successfully.");
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return 1;
        }
    }
}
