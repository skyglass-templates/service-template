package skyglass.servicetemplate.service.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public interface AccountCommandResult {

    public static final AccountCommandResult SUCCESS = new Success();
    public static final AccountCommandResult UNAUTHORIZED = new Unauthorized();

    public static AccountCommandResult createAccount(Long balance, String owner) {
        if (balance <= 0) {
            return new AmountNotGreaterThanZero(balance);
        } else {
            return new AccountCreationSuccessful(new Account(balance, owner));
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AccountCreationSuccessful implements AccountCommandResult {

        private Account account;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class AmountNotGreaterThanZero implements AccountCommandResult {

        private Long amount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BalanceExceeded implements AccountCommandResult {

        private Long amount;
        private Long balance;
    }


}

class Success implements AccountCommandResult {

}

class Unauthorized implements AccountCommandResult {
}



