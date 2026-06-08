package iteration1.api;

import api.generators.RandomData;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.UserRole;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.Endpoint;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;
import java.util.stream.Stream;

import static api.models.comparison.ModelAssertions.assertThatModels;
import static constants.ResponseMessage.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;


public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithCorrectData() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse createUserResponse = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(createUserRequest);
        assertThatModels(createUserRequest, createUserResponse).match();
    }

    private static Stream<Arguments> userInvalidData() {
        return Stream.of(
                Arguments.of(null, RandomData.getPassword(), "username", List.of(BLANK_USERNAME.getMessage())),
                Arguments.of(randomAlphabetic(2), RandomData.getPassword(), "username", List.of(USERNAME_MUST_BE_BETWEEN.getMessage())),
                Arguments.of(randomAlphabetic(3) + "%", RandomData.getPassword(), "username", List.of(USERNAME_MUST_CONTAIN_ONLY.getMessage())),
                Arguments.of(RandomData.getUsername(), null, "password", List.of(BLANK_PASSWORD.getMessage())),
                Arguments.of(RandomData.getUsername(), randomAlphanumeric(1), "password", List.of(PASSWORD_MUST_CONTAIN.getMessage()))
        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCanNotCreateUserWithInvalidData(String username, String password, String errorKey, List<String> errorValue) {

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.name())
                .build();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue)
        ).post(createUserRequest);
    }
}
