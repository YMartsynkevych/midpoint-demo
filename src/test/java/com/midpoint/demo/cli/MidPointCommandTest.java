package com.midpoint.demo.cli;

import com.midpoint.demo.cli.client.commands.SearchCommand;
import com.midpoint.demo.cli.client.commands.UpdateCommand;
import com.midpoint.demo.cli.client.commands.base.MidPointCommand;
import com.midpoint.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MidPointCommandTest {

    private UserService userService;
    private MidPointCommand midPointCommand;
    private CommandLine cmd;
    private StringWriter sw;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        midPointCommand = new MidPointCommand();

        CommandLine.IFactory factory = new CommandLine.IFactory() {
            @Override
            public <K> K create(Class<K> cls) throws Exception {
                if (cls == SearchCommand.class) {
                    return (K) new SearchCommand(userService);
                }
                if (cls == UpdateCommand.class) {
                    return (K) new UpdateCommand(userService);
                }
                return CommandLine.defaultFactory().create(cls);
            }
        };

        cmd = new CommandLine(midPointCommand, factory);
        sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));
        cmd.setErr(new PrintWriter(sw));
    }

    @Test
    void testSearchCommandParsingWithUsername() {
        int exitCode = cmd.execute("search", "testuser");
        assertEquals(0, exitCode);
        verify(userService).searchByUsername("testuser");
    }

    @Test
    void testSearchCommandParsingNoUsername() {
        int exitCode = cmd.execute("search");
        assertEquals(0, exitCode);
        verify(userService).searchByUsername(null);
    }

    @Test
    void testUpdateCommandParsingWithAllOptions() {
        int exitCode = cmd.execute("update",
                "--username", "testuser",
                "--email", "new@email.com",
                "--givenName", "John",
                "--familyName", "Doe",
                "--newName", "jdoe");
        assertEquals(0, exitCode);
        verify(userService).updateUserByUsername("testuser", "new@email.com", "John", "Doe", "jdoe");
    }

    @Test
    void testUpdateCommandMissingRequiredOption() {
        int exitCode = cmd.execute("update", "--email", "new@example.com");
        assertTrue(exitCode != 0);
        assertTrue(sw.toString().contains("Missing required option: '--username=<username>'"));
    }
}
