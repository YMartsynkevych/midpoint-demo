package com.midpoint.demo.cli;

import com.midpoint.demo.cli.client.commands.SearchCommand;
import com.midpoint.demo.cli.client.commands.UpdateCommand;
import com.midpoint.demo.cli.client.commands.base.MidPointCommand;
import com.midpoint.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MidPointCommandTest {

    @Mock
    private UserService userService;

    @Test
    void shouldExecuteSearchCommand_withPositionalParameter() {
        SearchCommand searchCommand = new SearchCommand(userService);
        CommandLine cmd = new CommandLine(searchCommand);

        when(userService.searchByUsername("testuser")).thenReturn(List.of());

        int exitCode = cmd.execute("testuser");

        assertEquals(0, exitCode);
        verify(userService).searchByUsername("testuser");
    }

    @Test
    void shouldExecuteUpdateCommand_withFlags() {
        UpdateCommand updateCommand = new UpdateCommand(userService);
        CommandLine cmd = new CommandLine(updateCommand);

        int exitCode = cmd.execute("--username", "testuser", "--email", "new@test.com");

        assertEquals(0, exitCode);
        verify(userService).updateUserByUsername(eq("testuser"), eq("new@test.com"), any(), any(), any());
    }

    @Test
    void shouldFailUpdateCommand_missingRequiredUsername() {
        UpdateCommand updateCommand = new UpdateCommand(userService);
        CommandLine cmd = new CommandLine(updateCommand);

        int exitCode = cmd.execute("--email", "new@test.com");

        assertEquals(2, exitCode);
    }

    @Test
    void shouldShowHelp_forMainCommand() {
        MidPointCommand mainCommand = new MidPointCommand();
        CommandLine cmd = new CommandLine(mainCommand);

        int exitCode = cmd.execute("--help");

        assertEquals(0, exitCode);
    }
}
