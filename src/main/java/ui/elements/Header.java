package ui.elements;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.pages.LoginPage;
import ui.pages.ProfilePage;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class Header {
    private final SelenideElement profileName = $("span.user-name");
    private final SelenideElement userNameWithDog = $("span.user-username");
    private final SelenideElement logoutButton = $(Selectors.byText("\uD83D\uDEAA Logout"));

    public ProfilePage goToProfilePage() {
        profileName.click();
        return Selenide.page(ProfilePage.class);
    }

    public LoginPage logout() {
        logoutButton.click();
        return Selenide.page(LoginPage.class);
    }
}
