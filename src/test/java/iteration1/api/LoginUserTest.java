package iteration1.api;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.LoginUserRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import api.Endpoint;
import api.requesters.CrudRequester;
import api.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {

    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest userRequest = LoginUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(userRequest);
    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequest user = AdminSteps.createUser();

        new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        ).post(
                LoginUserRequest.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .build()
        ).header("Authorization", Matchers.notNullValue());
    }
}
