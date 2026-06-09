package com.midpoint.demo.cli;

import com.midpoint.demo.service.UserService;
import com.midpoint.demo.client.MidPointClient;
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
    private MidPointClient midPointClient;
    private MidPointCommand midPointCommand;
    private CommandLine cmd;
    private StringWriter sw;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        midPointClient = mock(MidPointClient.class);
        midPointCommand = new MidPointCommand(midPointClient);

        CommandLine.IFactory factory = new CommandLine.IFactory() {
            @Override
            public <K> K create(Class<K> cls) throws Exception {
                if (cls == MidPointCommand.SearchCommand.class) {
                    return (K) new MidPointCommand.SearchCommand(userService);
                }
                if (cls == MidPointCommand.UpdateCommand.class) {
                    return (K) new MidPointCommand.UpdateCommand(userService);
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
    void testSearchCommandParsing() {
        int exitCode = cmd.execute("search", "--username", "testuser");
        assertEquals(0, exitCode);
        verify(userService).searchByUsername("testuser");
    }

    @Test
    void testUpdateCommandParsing() {
        int exitCode = cmd.execute("update", "--username", "testuser", "--email", "new@example.com");
        assertEquals(0, exitCode);
        verify(userService).updateUserByUsername(eq("testuser"), eq("new@example.com"), any(), any(), any());
    }

    @Test
    void testUpdateCommandMissingRequiredOption() {
        int exitCode = cmd.execute("update", "--email", "new@example.com");
        assertTrue(exitCode != 0);
        assertTrue(sw.toString().contains("Missing required option: '--username=<username>'"));
    }
}
