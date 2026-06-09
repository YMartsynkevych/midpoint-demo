package com.midpoint.demo.cli.client.commands.base;

import com.midpoint.demo.cli.client.commands.SearchCommand;
import com.midpoint.demo.cli.client.commands.UpdateCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Component
@Command(name = "midpoint-cli", mixinStandardHelpOptions = true, version = "1.0",
        description = "MidPoint User Management CLI",
        subcommands = {
                SearchCommand.class,
                UpdateCommand.class
        })
public class MidPointCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }
}
