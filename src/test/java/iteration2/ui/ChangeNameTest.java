package iteration2.ui;

import api.generators.RandomData;
import api.models.GetCustomerProfileResponse;
import com.codeborne.selenide.Condition;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import constants.BankAlert;
import iteration1.ui.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.ProfilePage;
import ui.pages.UserDashboard;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeNameTest extends BaseUiTest {

    @MethodSource("testdataproviders.ChangeNameDataProvider#validNameProvider")
    @ParameterizedTest
    @UserSession(testType = TestType.UI)
    @DisplayName("Ввод имени пользователя на валидное значение")
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
    }

    @MethodSource("testdataproviders.ChangeNameDataProvider#invalidNameProvider")
    @ParameterizedTest
    @UserSession(testType = TestType.UI)
    @DisplayName("Ввод имени пользователя на невалидное значение")
    public void userCanNotSetInvalidName(String newName) {
        new ProfilePage().open()
                .changeNameTo(newName)
                .checkAlertMessageAndAccept(BankAlert.ENTER_A_VALID_NAME.getMessage())
                .getHomeButton().goHome(UserDashboard.class)
                .getWelcomeText().shouldHave(Condition.text(
                        UserDashboard.WELCOME_TEXT.formatted(newName)
                ));

        GetCustomerProfileResponse getCustomerProfileResponse = SessionStorage.getSteps().getProfileInfo();
        assertThat(getCustomerProfileResponse.getName()).isNotEqualTo(newName);
    }

    @MethodSource("testdataproviders.ChangeNameDataProvider#validNameProvider")
    @ParameterizedTest
    @UserSession(testType = TestType.UI)
    @DisplayName("Изменение введённого имени пользователя на валидное значение")
    public void userCanUpdateValidName(String newName) {
        SessionStorage.getSteps().changeProfileName(RandomData.getCyrillicProfileName());

        new ProfilePage().open()
                .changeNameTo(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .getHomeButton().goHome(UserDashboard.class)
                .getWelcomeText().shouldHave(Condition.text(
                        UserDashboard.WELCOME_TEXT.formatted(newName)
                ));

        GetCustomerProfileResponse getCustomerProfileResponse = SessionStorage.getSteps().getProfileInfo();
        assertThat(getCustomerProfileResponse.getName()).isEqualTo(newName);
    }

    @Test
    @UserSession(testType = TestType.UI)
    @DisplayName("Изменение имени на то же самое")
    public void changeTheSameName() {
        String name = RandomData.getCyrillicProfileName();
        SessionStorage.getSteps().changeProfileName(name);

        new ProfilePage().open()
                .changeNameTo(name)
                .checkAlertMessageAndAccept(BankAlert.NAME_IS_THE_SAME.getMessage())
                .getHomeButton().goHome(UserDashboard.class)
                .getWelcomeText().shouldHave(Condition.text(
                        UserDashboard.WELCOME_TEXT.formatted(name)
                ));

        GetCustomerProfileResponse getCustomerProfileResponse = SessionStorage.getSteps().getProfileInfo();
        assertThat(getCustomerProfileResponse.getName()).isEqualTo(name);
    }

    @Test
    @UserSession(testType = TestType.UI)
    @DisplayName("Изменение имени на пустое")
    public void changeToEmptyName() {
        String name = RandomData.getCyrillicProfileName();
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
    }

}
