package skyglass.servicetemplate.security.keycloak;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import static java.text.MessageFormat.format;

public class RestAssuredUtils {

    public static RequestSpecification givenJwt(String jwt) {
        return RestAssured.given()
                .header("Authorization", format("Bearer {0}", jwt));
    }
}
