package ui.elements;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import lombok.Getter;
import org.openqa.selenium.Keys;
import ui.pages.LoginPage;
import ui.pages.ProfilePage;

import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.Keys.chord;

@Getter
public class Header {
    private final SelenideElement profileName = $("span.user-name");
    private final SelenideElement userNameWithDog = $("span.user-username");
    private final SelenideElement logoutButton = $(Selectors.byText("\uD83D\uDEAA Logout"));

    public ProfilePage goToProfilePage() {
        return StepLogger.log("Go to profile page",
                () -> {
                    profileName.click();
                    return Selenide.page(ProfilePage.class);
                }
        );
    }

    public LoginPage logout() {
        return StepLogger.log("Log out",
                () -> {
                    logoutButton.click();
                    return Selenide.page(LoginPage.class);
                }
        );
    }
}
