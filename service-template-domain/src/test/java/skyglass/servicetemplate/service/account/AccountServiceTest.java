package skyglass.servicetemplate.service.account;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension::class)
class AccountServiceTest {


    @Mock
    AccountRepository accountRepository;

    @Mock
    AuthenticatedUserSupplier authenticatedUserSupplier;

    @Mock
    AccountServiceObserver accountServiceObserver;

    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
       accountService = new AccountServiceImpl(accountRepository, authenticatedUserSupplier, accountServiceObserver);
    }

    @Test
    void shouldCreate() {
        val authenticatedUser = new AuthenticatedUser("user-1010", Collections.emptySet());
        when(authenticatedUserSupplier.get()).thenReturn(authenticatedUser);

        val savedAccount = ArgumentCaptor.forClass(Account.class);

        when(accountRepository.save(savedAccount.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        val outcome = accountService.createAccount(TestData.initialBalance);

        Assertions.assertThat(outcome).isEqualTo(AccountServiceCommandResult.Success(savedAccount.value))
    }

}