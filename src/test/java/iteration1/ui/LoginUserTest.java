package iteration1.ui;

import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanel;
import ui.pages.LoginPage;
import ui.pages.UserDashboard;

import java.time.Duration;

import static constants.DefaultProfileName.DEFAULT_PROFILE_NAME;

public class LoginUserTest extends BaseUiTest {
    @Test
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequest admin = CreateUserRequest.getAdmin();

        new LoginPage().open()
                .login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanel.class)
                .getAdminPanelText().shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    @Test
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequest user = AdminSteps.createUser();

        new LoginPage().open()
                .login(user.getUsername(), user.getPassword())
                .getPage(UserDashboard.class)
                .getWelcomeText().shouldBe(Condition.visible).shouldHave(Condition.text(
                        UserDashboard.WELCOME_TEXT.formatted(DEFAULT_PROFILE_NAME.getDefaultNameValue())
                ));
    }
}
