package api.iteration2;

import api.BaseTest;
import constants.ResponseMessage;
import models.CreateUserRequest;
import models.CustomerProfileRequest;
import models.GetCustomerProfileResponse;
import models.UpdateCustomerProfileResponse;
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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeNameTest extends BaseTest {

    @ValueSource(strings = {
            "Фамилия Имя",
            "Firstname Lastname"
    })
    @ParameterizedTest
    @DisplayName("Изменение имени пользователя на валидное значение")
    public void userCanUpdateValidName(String name) {

        CreateUserRequest user = AdminSteps.createUser();

        UpdateCustomerProfileResponse updateCustomerProfileResponse =
                new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                        RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                        Endpoint.UPDATE_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).update(new CustomerProfileRequest(name));
        assertThat(updateCustomerProfileResponse.getCustomer().getName()).isEqualTo(name);
        assertThat(updateCustomerProfileResponse.getMessage()).isEqualTo(
                ResponseMessage.PROFILE_UPDATED_SUCCESSFULLY.getMessage()
        );

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).get();
        assertThat(getCustomerProfileResponse.getName()).isEqualTo(name);
    }

    static Stream<String> nameProvider() {
        return Stream.of(
                "Фамилия Имя Отчество",
                "Firstname Lastname Middlename",
                randomAlphanumeric(5) + " " + randomAlphanumeric(5),
                randomNumeric(5) + " " + randomNumeric(5)
        );
    }

    @MethodSource("nameProvider")
    @ParameterizedTest
    @DisplayName("Невозможность изменения имени пользователя на невалидное значение")
    public void userCanNotUpdateInvalidName(String name) {
        CreateUserRequest user = AdminSteps.createUser();

        new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsBadRequest()
        ).update(new CustomerProfileRequest(name));

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).get();
        assertThat(getCustomerProfileResponse.getName()).isNotEqualTo(name);
    }
}