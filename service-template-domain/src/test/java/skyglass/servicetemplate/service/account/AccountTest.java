package skyglass.servicetemplate.service.account;

import lombok.val;
import org.junit.jupiter.api.Test;
import skyglass.servicetemplate.service.account.AccountCommandResult.AmountNotGreaterThanZero;
import skyglass.servicetemplate.service.account.AccountCommandResult.BalanceExceeded;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static skyglass.servicetemplate.domain.TestData.balanceAfterCredit;
import static skyglass.servicetemplate.domain.TestData.balanceAfterDebit;
import static skyglass.servicetemplate.domain.TestData.creditAmount;
import static skyglass.servicetemplate.domain.TestData.debitAmount;
import static skyglass.servicetemplate.domain.TestData.initialBalance;
import static skyglass.servicetemplate.service.account.AccountCommandResult.SUCCESS;

public class AccountTest {

    @Test
    public void shouldDebitAndCredit() {
        val account = new Account(initialBalance, "owner");
        val result = account.debit(debitAmount);

        assertThat(result).isEqualTo(SUCCESS);
        assertThat(account.getBalance()).isEqualTo(balanceAfterDebit);

        val creditResult = account.credit(creditAmount);
        assertThat(creditResult).isEqualTo(SUCCESS);
        assertThat(account.getBalance()).isEqualTo(balanceAfterCredit);
    }

    @Test
    public void shouldDebitCurrentBalance() {
        val account = new Account(initialBalance, "owner");
        val result = account.debit(initialBalance);
        assertThat(result).isEqualTo(SUCCESS);
        assertThat(account.getBalance()).isEqualTo(0);
   }

    @Test
    public void shouldDebitCurrentBalanceMinus1() {
        val account = new Account(initialBalance, "owner");
        val result = account.debit(initialBalance - 1);
        assertThat(result).isEqualTo(SUCCESS);
        assertThat(account.getBalance()).isEqualTo(1);
   }

    @Test
    public void shouldDebitCurrentBalancePlusShouldFail() {
        val account = new Account(initialBalance, "owner");
        val result = account.debit(initialBalance + 1);
        assertThat(result).isEqualTo(new BalanceExceeded(initialBalance + 1, initialBalance));
        assertThat(account.getBalance()).isEqualTo(initialBalance);
   }

    @Test
    public void debitZeroShouldFail() {
        val account = new Account(initialBalance, "owner");
        val amount = 0L;
        val result = account.debit(amount);
        assertThat(result).isEqualTo(new AmountNotGreaterThanZero(amount));
        assertThat(account.getBalance()).isEqualTo(initialBalance);
   }

    @Test
    public void creditZeroShouldFail() {
        val account = new Account(initialBalance, "owner");
        val amount= 0L;
        val result = account.credit(amount);
        assertThat(result).isEqualTo(new AmountNotGreaterThanZero(amount));
        assertThat(account.getBalance()).isEqualTo(initialBalance);
   }

}