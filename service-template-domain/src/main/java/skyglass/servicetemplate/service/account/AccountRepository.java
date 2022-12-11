package skyglass.servicetemplate.service.account;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Iterable<Account> findByOwner(String owner);
}