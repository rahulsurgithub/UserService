package dev.project.userservice.integration;

import dev.project.userservice.UserServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainRunsWithoutException() {
        // call main with empty args to ensure the application class can be started
        UserServiceApplication.main(new String[]{});
    }

}
