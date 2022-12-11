package skyglass.servicetemplate.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {


    private AccountRepository accountRepository;

    private AuthenticatedUserSupplier authenticatedUserSupplier = AuthenticatedUserSupplier.EMPTY_SUPPLIER;

    private AccountServiceObserver accountServiceObserver;

    public AccountServiceCommandResult createAccount(Long initialBalance) {
        AccountCommandResult outcome = AccountCommandResult.createAccount(initialBalance, currentUserId());
        if (outcome instanceof AccountCommandResult.AccountCreationSuccessful accountCreationSuccessful) {
            accountServiceObserver.noteAccountCreated();
            return new AccountServiceCommandResult.Success(accountRepository.save(accountCreationSuccessful.getAccount()));
        } else if (outcome instanceof AccountCommandResult.AmountNotGreaterThanZero) {
            return new AccountServiceCommandResult.AmountNotGreaterThanZero(initialBalance);
        } else {
            return new AccountServiceCommandResult.Unexpected(outcome);
        }
    }

    private String currentUserId() {
        return authenticatedUserSupplier.get().getId();
    }

    public AccountServiceCommandResult findAccount(Long id) {
        return withAuthorizedAccess(id, account -> new AccountServiceCommandResult.Success(account))
                .orElseGet(() -> AccountServiceCommandResult.ACCOUNT_NOT_FOUND);
    }

    public AccountServiceCommandResult debit(Long id, Long amount) {
        val result = withAuthorizedAccess(id,
                account -> {
                    val outcome = account.credit(amount);
                    if (outcome == AccountCommandResult.SUCCESS) {
                        accountServiceObserver.noteSuccessfulDebit();
                        return new AccountServiceCommandResult.Success(account);
                    } else if (outcome instanceof AccountCommandResult.AmountNotGreaterThanZero) {
                        accountServiceObserver.noteFailedDebit();
                        return new AccountServiceCommandResult.AmountNotGreaterThanZero(amount);
                    } else if (outcome instanceof AccountCommandResult.BalanceExceeded balanceExceeded) {
                        accountServiceObserver.noteFailedDebit();
                        return new AccountServiceCommandResult.BalanceExceeded(amount, balanceExceeded.getBalance());
                    } else if (outcome == AccountCommandResult.UNAUTHORIZED) {
                        return AccountServiceCommandResult.UNAUTHORIZED;
                    } else {
                        return new AccountServiceCommandResult.Unexpected(outcome);
                    }
                });
        if (result.isEmpty()) {
            accountServiceObserver.noteFailedDebit();
            return AccountServiceCommandResult.ACCOUNT_NOT_FOUND;
        }
        return result.get();
    }


    public AccountServiceCommandResult credit(Long id, Long amount) {
        val result = withAuthorizedAccess(id,
                account -> {
                    val outcome = account.credit(amount);
                    if (outcome == AccountCommandResult.SUCCESS) {
                        accountServiceObserver.noteSuccessfulCredit();
                        return new AccountServiceCommandResult.Success(account);
                    } else if (outcome instanceof AccountCommandResult.AmountNotGreaterThanZero) {
                        accountServiceObserver.noteFailedCredit();
                        return new AccountServiceCommandResult.AmountNotGreaterThanZero(amount);
                    } else if (outcome == AccountCommandResult.UNAUTHORIZED) {
                        return AccountServiceCommandResult.UNAUTHORIZED;
                    } else {
                        return new AccountServiceCommandResult.Unexpected(outcome);
                    }
                });
        if (result.isEmpty()) {
            accountServiceObserver.noteFailedCredit();
            return AccountServiceCommandResult.ACCOUNT_NOT_FOUND;
        }
        return result.get();
    }

    public List<Account> findAllAccounts() {
        return IterableUtils.toList(accountRepository.findByOwner(currentUserId()));
    }

    private Optional<AccountServiceCommandResult> withAuthorizedAccess(Long id, Function<Account, AccountServiceCommandResult> function) {
        return accountRepository.findById(id).map(account ->
                account.getOwner() != currentUserId() ? AccountServiceCommandResult.UNAUTHORIZED : function.apply(account)
        );
    }
}

