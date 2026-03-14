package iteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.name())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        ).post(userRequest);

        // создаем аккаунт (счёт)
        int accountId = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated()
        )
                .post(null)
                .extract()
                .path("id");


//        //запросить все аккаунты пользователя и проверить, что наш аккаунт там
//        given()
//                .header("Authorization", userAuthHeader)
//                .contentType(ContentType.JSON)
//                .accept(ContentType.JSON)
//                .get("http://localhost:4111/api/v1/customer/accounts")
//                .then()
//                .assertThat()
//                .statusCode(HttpStatus.SC_OK)
//                .body("id", Matchers.hasItem(accountId));
    }
}
