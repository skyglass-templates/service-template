package skyglass.servicetemplate.security.keycloak;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.hamcrest.CoreMatchers;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;

@Component
@ConditionalOnProperty("keycloak.auth-server-url")
class JwtProvider {

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Autowired
    private URI httpProxy;

    private Map<String, String> jwts = new HashMap<>();

    private Map<String, String> ids = new HashMap<>();

    private String getUserId(String userName) {
        return ids.get(userName);
    }

    private String getJwt(String userName, String password, String role) {
        if (!jwts.containsKey(userName)) {
            ensureUserExists(userName, password, role);

            jwts.put(userName, fetchJwt(clientId, userName, password));

        }
        System.out.println("Issued jwt for $userName = ${jwts[userName]}");
        return jwts.get(userName);
    }

    private void ensureUserExists(String userName, String password, String role) {
        val keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();

        val realmResource = keycloak.realm(realm);
        val usersResource = realmResource.users();

        val userr = new UserRepresentation();
        userr.setUsername(userName);
        userr.setEnabled(true);

        val createUserResponse = usersResource.create(userr);

        assertThat(createUserResponse.getStatus()).isIn(201, 409);

        val userId = usersResource.search(userName).get(0).getId();

        val userResource = usersResource.get(userId);

        ids.put(userName, userId);

        val credr = new CredentialRepresentation();
        credr.setType(CredentialRepresentation.PASSWORD);
        credr.setValue(password);
        credr.setTemporary(false);

        userResource.resetPassword(credr);

        val rolesResource = realmResource.roles();

        val serviceTemplateRealmRole = rolesResource.get(role).toRepresentation();
        userResource.roles().realmLevel().add(Arrays.asList(serviceTemplateRealmRole));
    }

    /*


    curl -X POST \
           -d "grant_type=password&username=foo&password=foopassword&client_id=service-template" \
           http://localhost:8091/auth/realms/service-template/protocol/openid-connect/token

     */
    private String fetchJwt(String clientId, String userName, String password) {
        var ra = RestAssured.given();
        if (httpProxy != null) {
            ra = ra.proxy(httpProxy);
        }
        return ra
                .urlEncodingEnabled(true)
                .param("client_id", clientId)
                .param("username", userName)
                .param("password", password)
                .param("grant_type", "password")
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .post(format("{0}/realms/{1}/protocol/openid-connect/token", keycloakUrl, realm))
                .then()
                .statusCode(200)
                .assertThat()
                .body("access_token", CoreMatchers.not(CoreMatchers.nullValue()))
                .extract()
                .path("access_token");
    }

    private String jwtForAuthorizedUser() {
        return getJwt(TestUserCredentials.userName, TestUserCredentials.password, "service-template-user");
    }

    private String jwtForOtherAuthorizedUser() {
        return getJwt(TestUserCredentials.otherAuthorizedUserName, TestUserCredentials.password, "service-template-user");
    }

    private String jwtForUserInSomeOtherRole() {
        return getJwt(TestUserCredentials.userNameOther, TestUserCredentials.passwordOther, "some-other-role");
    }


    private static class TestUserCredentials {
        public static final String userName = "foo";
        public static final String password = "foopassword";
        public static final String otherAuthorizedUserName = "fooOtherAuthorized";
        public static final String userNameOther = "fooOther";
        public static final String passwordOther = "foopasswordOther";
    }


}





