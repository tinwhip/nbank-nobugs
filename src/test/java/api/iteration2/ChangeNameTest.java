package api.iteration2;

import api.BaseTest;
import constants.ResponseMessage;
import generators.RandomData;
import models.CreateUserRequest;
import models.CustomerProfileRequest;
import models.GetCustomerProfileResponse;
import models.UpdateCustomerProfileResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.CrudRequester;
import requests.skeleton.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static generators.RandomData.getCyrillicString;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeNameTest extends BaseTest {
    private static final String VALID_NAME_PLACEHOLDER = "%s %s";
    private static final String INVALID_NAME_PLACEHOLDER = "%s %s %s";

    static Stream<String> validNameProvider() {
        return Stream.of(
                VALID_NAME_PLACEHOLDER.formatted(getCyrillicString(5), getCyrillicString(5)),
                VALID_NAME_PLACEHOLDER.formatted(randomAlphabetic(5), randomAlphabetic(5))
        );
    }

    @MethodSource("validNameProvider")
    @ParameterizedTest
    @DisplayName("Изменение имени пользователя на валидное значение")
    public void userCanUpdateValidName(String name) {
        CreateUserRequest user = AdminSteps.createUser();

        UpdateCustomerProfileResponse updateCustomerProfileResponse =
                new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                        RequestSpecs.authAsUser(user),
                        Endpoint.UPDATE_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).update(new CustomerProfileRequest(name));
        assertThat(updateCustomerProfileResponse.getCustomer().getName()).isEqualTo(name);
        assertThat(updateCustomerProfileResponse.getMessage()).isEqualTo(
                ResponseMessage.PROFILE_UPDATED_SUCCESSFULLY.getMessage()
        );

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        RequestSpecs.authAsUser(user),
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).get();
        assertThat(getCustomerProfileResponse.getName()).isEqualTo(name);
    }

    static Stream<String> invalidNameProvider() {
        return Stream.of(
                INVALID_NAME_PLACEHOLDER.formatted(
                        getCyrillicString(5), getCyrillicString(5), getCyrillicString(5)
                ),
                INVALID_NAME_PLACEHOLDER.formatted(
                        randomAlphabetic(5), randomAlphabetic(5), randomAlphabetic(5)
                ),
                VALID_NAME_PLACEHOLDER.formatted(randomAlphanumeric(5),randomAlphanumeric(5)),
                VALID_NAME_PLACEHOLDER.formatted(randomNumeric(5), randomNumeric(5))
        );
    }

    @MethodSource("invalidNameProvider")
    @ParameterizedTest
    @DisplayName("Невозможность изменения имени пользователя на невалидное значение")
    public void userCanNotUpdateInvalidName(String name) {
        CreateUserRequest user = AdminSteps.createUser();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsBadRequest()
        ).update(new CustomerProfileRequest(name));

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        RequestSpecs.authAsUser(user),
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).get();
        assertThat(getCustomerProfileResponse.getName()).isNotEqualTo(name);
    }
}