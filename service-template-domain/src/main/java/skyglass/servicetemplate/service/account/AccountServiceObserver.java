package skyglass.servicetemplate.service.account;

public interface AccountServiceObserver {

    void noteAccountCreated();
    void noteSuccessfulDebit();
    void noteFailedDebit();
    void noteFailedCredit();
    void noteSuccessfulCredit();
    void noteUnauthorizedAccountAccess();
}
