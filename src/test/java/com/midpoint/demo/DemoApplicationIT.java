package com.midpoint.demo;

import com.midpoint.demo.cli.client.MidPointClient;
import com.midpoint.demo.cli.client.commands.base.MidPointCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(args = "--help")
class DemoApplicationIT {

    @Autowired
    private DemoApplication demoApplication;

    @Autowired
    private MidPointCommand midPointCommand;

    @MockitoBean
    private MidPointClient midPointClient;

    @Test
    void contextLoads() {
        assertNotNull(demoApplication);
        assertNotNull(midPointCommand);
    }

    @Test
    void shouldExecuteWithHelpArgument() {
        assertNotNull(demoApplication);
    }
}
