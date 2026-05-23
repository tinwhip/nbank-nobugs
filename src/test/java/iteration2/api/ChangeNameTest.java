package iteration2.api;

import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import iteration1.api.BaseTest;
import api.models.CreateUserRequest;
import api.models.CustomerProfileRequest;
import api.models.GetCustomerProfileResponse;
import api.models.UpdateCustomerProfileResponse;
import constants.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import api.Endpoint;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.stream.Stream;

import static api.generators.RandomData.getCyrillicString;
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
    @UserSession(testType = TestType.API)
    public void userCanUpdateValidName(String name) {
        UpdateCustomerProfileResponse updateCustomerProfileResponse =
                new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                        RequestSpecs.authAsUser(SessionStorage.getUser()),
                        Endpoint.UPDATE_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).update(new CustomerProfileRequest(name));
        assertThat(updateCustomerProfileResponse.getCustomer().getName()).isEqualTo(name);
        assertThat(updateCustomerProfileResponse.getMessage()).isEqualTo(
                ResponseMessage.PROFILE_UPDATED_SUCCESSFULLY.getMessage()
        );

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        RequestSpecs.authAsUser(SessionStorage.getUser()),
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
    @UserSession(testType = TestType.API)
    public void userCanNotUpdateInvalidName(String name) {
        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser()),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsBadRequest()
        ).update(new CustomerProfileRequest(name));

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        RequestSpecs.authAsUser(SessionStorage.getUser()),
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).get();
        assertThat(getCustomerProfileResponse.getName()).isNotEqualTo(name);
    }
}