package com.project.userservice;

import com.project.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        assertNotNull(context, "The application context should have been loaded");
    }

    @Test
    void userServiceBeanExists() {
        assertNotNull(userService, "UserService bean should be present in the context");
    }

    @Test
    void mainRuns() {
        // Should not throw any exception
        UserServiceApplication.main(new String[]{});
    }

}
