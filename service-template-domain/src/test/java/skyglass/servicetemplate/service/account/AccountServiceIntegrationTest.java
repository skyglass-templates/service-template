package skyglass.servicetemplate.service.account;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = {AccountServiceIntegrationTest.class, AccountServiceIntegrationTest.Config.class})
class AccountServiceIntegrationTest {

    private AccountService accountService;

    @Configuration
    @ComponentScan
    static class Config {

    }

    @MockBean
    private AccountRepository accountRepository;

    @Test
    void shouldConfigure() {

    }

}