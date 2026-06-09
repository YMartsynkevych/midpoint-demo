package com.midpoint.demo.cli;

import com.midpoint.demo.model.User;
import com.midpoint.demo.service.UserService;
import com.midpoint.demo.client.MidPointClient;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "midpoint-cli", mixinStandardHelpOptions = true, version = "1.0",
        description = "MidPoint User Management CLI",
        subcommands = {
                MidPointCommand.SearchCommand.class,
                MidPointCommand.UpdateCommand.class
        })
public class MidPointCommand implements Callable<Integer> {

    private final MidPointClient midPointClient;

    public MidPointCommand(MidPointClient midPointClient) {
        this.midPointClient = midPointClient;
    }

    public MidPointClient getMidPointClient() {
        return midPointClient;
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    @Component
    @Command(name = "search", description = "Search users by username")
    public static class SearchCommand implements Callable<Integer> {
        private static final int COL_WIDTH_DEFAULT = 15;
        private static final int COL_WIDTH_EMAIL = 20;
        private static final String TABLE_FORMAT = "%-15s | %-15s | %-15s | %-15s | %-20s%n";
        private static final String TABLE_SEPARATOR = "----------------------------------------------------------------------------------------------------";

        @CommandLine.ParentCommand
        private MidPointCommand parent;

        private final UserService userService;

        @Option(names = {"--username"}, description = "Username to search for")
        private String username;

        public SearchCommand(UserService userService) {
            this.userService = userService;
        }

        @Override
        public Integer call() {
            try {
                List<User> users = userService.searchByUsername(username);
                if (users == null || users.isEmpty()) {
                    System.out.println("No users found for username: " + username);
                } else {
                    System.out.println("Found " + users.size() + " user(s):");
                    renderTable(users);
                }
                return 0;
            } catch (Exception e) {
                System.err.println("Error searching users: " + e.getMessage());
                return 1;
            }
        }

        private void renderTable(List<User> users) {
            System.out.println(TABLE_SEPARATOR);
            System.out.printf(TABLE_FORMAT, "OID", "Name", "Given Name", "Family Name", "Email");
            System.out.println(TABLE_SEPARATOR);
            for (User user : users) {
                System.out.printf(TABLE_FORMAT,
                        truncate(user.getOid(), COL_WIDTH_DEFAULT),
                        truncate(user.getName(), COL_WIDTH_DEFAULT),
                        truncate(user.getGivenName(), COL_WIDTH_DEFAULT),
                        truncate(user.getFamilyName(), COL_WIDTH_DEFAULT),
                        truncate(user.getEmailAddress(), COL_WIDTH_EMAIL));
            }
            System.out.println(TABLE_SEPARATOR);
        }

        private String truncate(String value, int length) {
            if (value == null) return "";
            return value.length() <= length ? value : value.substring(0, length - 3) + "...";
        }
    }

    @Component
    @Command(name = "update", description = "Update user fields by username")
    public static class UpdateCommand implements Callable<Integer> {
        @CommandLine.ParentCommand
        private MidPointCommand parent;

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
}
