package skyglass.servicetemplate.domain;

public class TestData {

    public static final Long accountId = 99L;
    public static final Long initialBalance = 101L;
    public static final Long debitAmount = 11L;
    public static final Long creditAmount = 5L;
    public static final Long balanceAfterDebit = initialBalance - debitAmount;
    public static final Long balanceAfterCredit = balanceAfterDebit + creditAmount;

}