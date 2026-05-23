package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public abstract class AuthorizedPage<T extends AuthorizedPage> extends BasePage<T> {
    protected final SelenideElement userName = $("span.user-name");
    protected final SelenideElement userUsername = $("span.user-username");
    protected SelenideElement logoutButton = $(Selectors.byText("\uD83D\uDEAA Logout"));

    public T goToProfilePage() {
        userName.click();
        return (T) this;
    }

    public T logout() {
        logoutButton.click();
        return (T) this;
    }
}
