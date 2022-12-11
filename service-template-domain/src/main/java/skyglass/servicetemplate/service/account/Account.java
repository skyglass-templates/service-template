package skyglass.servicetemplate.service.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import skyglass.servicetemplate.domain.AbstractBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "account")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Account extends AbstractBaseEntity {

    @Column(name = "balance")
    private Long balance;
    @Column(name = "owner")
    private String owner;

    public AccountCommandResult debit(Long amount) {
        if (amount <= 0)
            return new AccountCommandResult.AmountNotGreaterThanZero(amount);

        if (amount > balance)
            return new AccountCommandResult.BalanceExceeded(amount, balance);

        balance -= amount;

        return AccountCommandResult.SUCCESS;
    }

    public AccountCommandResult credit(Long amount) {
        if (amount <= 0)
            return new AccountCommandResult.AmountNotGreaterThanZero(amount);

        balance += amount;

        return AccountCommandResult.SUCCESS;
    }
}
