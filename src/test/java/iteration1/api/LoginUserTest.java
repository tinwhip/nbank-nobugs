package iteration1.api;

import api.models.*;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import api.Endpoint;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginUserTest extends BaseTest {

    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest userRequest = LoginUserRequest.builder()
                .username(CreateUserRequest.getAdmin().getUsername())
                .password(CreateUserRequest.getAdmin().getPassword())
                .build();

        LoginUserResponse authResponse = new ValidatedCrudRequester<LoginUserResponse>(RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(userRequest);
        assertThat(authResponse.getUsername()).isEqualTo(userRequest.getUsername());
        assertThat(authResponse.getRole()).isEqualTo(UserRole.ADMIN.name());
    }

    @Test
    @UserSession(testType = TestType.API)
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequest user = SessionStorage.getUser();

        new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        ).post(LoginUserRequest.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .build()
                ).header("Authorization", Matchers.notNullValue())
                .body("username", Matchers.equalTo(user.getUsername()))
                .body("role", Matchers.equalTo(UserRole.USER.name()));

        List<GetCustomerProfileResponse> customers = new ValidatedCrudRequester<GetCustomerProfileResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOK()
        ).getAll(GetCustomerProfileResponse[].class);

        assertThat(
                customers.stream()
                        .filter(
                                customer -> customer.getUsername().equals(user.getUsername())
                        ).toList()
        ).hasSize(1);
        System.out.println();
    }
}
