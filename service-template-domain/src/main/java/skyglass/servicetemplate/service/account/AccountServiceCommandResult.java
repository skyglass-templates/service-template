package skyglass.servicetemplate.service.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public interface AccountServiceCommandResult {

    public static final AccountServiceCommandResult ACCOUNT_NOT_FOUND = new AccountNotFound();
    public static final AccountServiceCommandResult  UNAUTHORIZED = new UnauthorizedServiceCommandResult();

    @Getter
    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Success implements AccountServiceCommandResult {

        private Account account;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Unexpected implements AccountServiceCommandResult {

        private AccountCommandResult outcome;


    }

    @Getter
    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class AmountNotGreaterThanZero implements AccountServiceCommandResult {

        private Long amount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class BalanceExceeded implements AccountServiceCommandResult {

        private Long amount;
        private Long balance;
    }



}

class AccountNotFound implements AccountServiceCommandResult {

}

class UnauthorizedServiceCommandResult implements AccountServiceCommandResult {
}