package skyglass.servicetemplate.service.account;

import java.util.Collections;
import java.util.function.Supplier;

public interface AuthenticatedUserSupplier extends Supplier<AuthenticatedUser> {

    public static AuthenticatedUserSupplier EMPTY_SUPPLIER = () -> new AuthenticatedUser("nullId", Collections.emptySet());

}
