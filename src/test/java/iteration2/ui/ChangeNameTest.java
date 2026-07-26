package iteration2.ui;

import api.generators.RandomData;
import api.models.GetCustomerProfileResponse;
import com.codeborne.selenide.Condition;
import common.TestType;
import common.annotations.ApiVersion;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import constants.BankAlert;
import constants.DefaultProfileName;
import constants.ResponseMessage;
import db.entity.comparison.DaoAndModelAssertions;
import iteration1.ui.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.ProfilePage;
import ui.pages.UserDashboard;

import static db.steps.CustomerTableSteps.getUserByUsername;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeNameTest extends BaseUiTest {

    @MethodSource("testdataproviders.ChangeNameDataProvider#validNameProvider")
    @ParameterizedTest
    @UserSession(testType = TestType.UI)
    @DisplayName("Ввод имени пользователя на валидное значение")
    @ApiVersion(version = "with_database_with_fix")
    public void userCanSetValidName(String newName) {
        new ProfilePage().open()
                .changeNameTo(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .getHomeButton().goHome(UserDashboard.class)
                .getWelcomeText().shouldHave(Condition.text(
                        UserDashboard.WELCOME_TEXT.formatted(newName)
                ));

        GetCustomerProfileResponse getCustomerProfileResponse = SessionStorage.getSteps().getProfileInfo();
        assertThat(getCustomerProfileResponse.getName()).isEqualTo(newName);
        DaoAndModelAssertions.assertThat(
                getCustomerProfileResponse, getUserByUsername(SessionStorage.getUser().getUsername())
        ).match();
    }

    @MethodSource("testdataproviders.ChangeNameDataProvider#invalidNameProvider")
    @ParameterizedTest
    @UserSession(testType = TestType.UI)
    @DisplayName("Ввод имени пользователя на невалидное значение")
    @ApiVersion(version = "with_database_with_fix")
    public void userCanNotSetInvalidName(String newName) {
        new ProfilePage().open()
                .changeNameTo(newName)
                .checkAlertMessageAndAccept(ResponseMessage.NAME_MUST_CONTAIN_TWO_WORDS.getMessage())
                .getHomeButton().goHome(UserDashboard.class)
                .getWelcomeText().shouldHave(Condition.text(
                        DefaultProfileName.DEFAULT_PROFILE_NAME.getDefaultNameValue()
                ));

        GetCustomerProfileResponse getCustomerProfileResponse = SessionStorage.getSteps().getProfileInfo();
        assertThat(getCustomerProfileResponse.getName()).isNotEqualTo(newName);
        DaoAndModelAssertions.assertThat(
                getCustomerProfileResponse, getUserByUsername(SessionStorage.getUser().getUsername())
        ).match();
    }

    @MethodSource("testdataproviders.ChangeNameDataProvider#validNameProvider")
    @ParameterizedTest
    @UserSession(testType = TestType.UI)
    @DisplayName("Изменение введённого имени пользователя на валидное значение")
    @ApiVersion(version = "with_database_with_fix")
    public void userCanUpdateValidName(String newName) {
        SessionStorage.getSteps().changeProfileName(RandomData.getLatinProfileName());

        new ProfilePage().open()
                .changeNameTo(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .getHomeButton().goHome(UserDashboard.class)
                .getWelcomeText().shouldHave(Condition.text(
                        UserDashboard.WELCOME_TEXT.formatted(newName)
                ));

        GetCustomerProfileResponse getCustomerProfileResponse = SessionStorage.getSteps().getProfileInfo();
        assertThat(getCustomerProfileResponse.getName()).isEqualTo(newName);
        DaoAndModelAssertions.assertThat(
                getCustomerProfileResponse, getUserByUsername(SessionStorage.getUser().getUsername())
        ).match();
    }

    @Test
    @UserSession(testType = TestType.UI)
    @DisplayName("Изменение имени на то же самое")
    @ApiVersion(version = "with_database_with_fix")
    public void changeTheSameName() {
        String name = RandomData.getLatinProfileName();
        SessionStorage.getSteps().changeProfileName(name);

        new ProfilePage().open()
                .changeNameTo(name)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .getHomeButton().goHome(UserDashboard.class)
                .getWelcomeText().shouldHave(Condition.text(
                        UserDashboard.WELCOME_TEXT.formatted(name)
                ));

        GetCustomerProfileResponse getCustomerProfileResponse = SessionStorage.getSteps().getProfileInfo();
        assertThat(getCustomerProfileResponse.getName()).isEqualTo(name);
        DaoAndModelAssertions.assertThat(
                getCustomerProfileResponse, getUserByUsername(SessionStorage.getUser().getUsername())
        ).match();
    }

    @Test
    @UserSession(testType = TestType.UI)
    @DisplayName("Изменение имени на пустое")
    @ApiVersion(version = "with_database_with_fix")
    public void changeToEmptyName() {
        String name = RandomData.getLatinProfileName();
        SessionStorage.getSteps().changeProfileName(name);
        new UserDashboard().open();
        new ProfilePage().open()
                .clear()
                .saveChanges()
                .checkAlertMessageAndAccept(BankAlert.ENTER_A_VALID_NAME.getMessage())
                .getHomeButton().goHome(UserDashboard.class)
                .getWelcomeText().shouldHave(Condition.text(
                        UserDashboard.WELCOME_TEXT.formatted(name)
                ));

        GetCustomerProfileResponse getCustomerProfileResponse = SessionStorage.getSteps().getProfileInfo();
        assertThat(getCustomerProfileResponse.getName()).isEqualTo(name);
        DaoAndModelAssertions.assertThat(
                getCustomerProfileResponse, getUserByUsername(SessionStorage.getUser().getUsername())
        ).match();
    }

}
