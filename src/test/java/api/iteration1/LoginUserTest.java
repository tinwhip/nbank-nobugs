package api.iteration1;

import api.BaseTest;
import constants.AdminCredentials;
import models.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.CrudRequester;
import requests.skeleton.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginUserTest extends BaseTest {

    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest userRequest = LoginUserRequest.builder()
                .username(AdminCredentials.CREDENTIALS.getUsername())
                .password(AdminCredentials.CREDENTIALS.getPassword())
                .build();

        LoginUserResponse authResponse = new ValidatedCrudRequester<LoginUserResponse>(RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(userRequest);
        assertThat(authResponse.getUsername()).isEqualTo(userRequest.getUsername());
        assertThat(authResponse.getRole()).isEqualTo(UserRole.ADMIN.name());
    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequest user = AdminSteps.createUser();

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
                        .filter(customer -> customer.getUsername().equals(user.getUsername()))
                        .toList()
        ).hasSize(1);
    }
}
