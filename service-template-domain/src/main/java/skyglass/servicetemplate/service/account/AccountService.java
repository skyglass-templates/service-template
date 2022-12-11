package skyglass.servicetemplate.service.account;

import java.util.List;

public interface AccountService {

    AccountServiceCommandResult createAccount(Long initialBalance);
    AccountServiceCommandResult  findAccount(Long id);
    AccountServiceCommandResult  debit(Long id, Long amount);
    AccountServiceCommandResult  credit(Long id, Long amount);
    List<Account> findAllAccounts();
}
