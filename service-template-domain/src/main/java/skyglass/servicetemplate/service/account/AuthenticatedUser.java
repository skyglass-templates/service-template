package skyglass.servicetemplate.service.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class AuthenticatedUser {

    private String id;
    private Set<String> roles;
}
