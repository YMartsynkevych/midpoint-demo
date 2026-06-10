package com.midpoint.demo.cli.client.commands;

import com.midpoint.demo.domain.User;
import com.midpoint.demo.service.UserService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "search", description = "Search users by username")
public class SearchCommand implements Callable<Integer> {
    private static final int COL_WIDTH_DEFAULT = 15;
    private static final int COL_WIDTH_EMAIL = 20;
    private static final String TABLE_FORMAT = "%-15s | %-15s | %-15s | %-15s | %-20s%n";
    private static final String TABLE_SEPARATOR = "----------------------------------------------------------------------------------------------------";

    private final UserService userService;

    @Parameters(index = "0", description = "Username to search for", arity = "0..1")
    private String username;

    public SearchCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Integer call() {
        try {
            List<User> users = userService.searchByUsername(username);
            if (users == null || users.isEmpty()) {
                System.out.println("No users found for username: " + (username != null ? username : "all"));
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
