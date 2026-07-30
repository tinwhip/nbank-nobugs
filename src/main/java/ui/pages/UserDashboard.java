package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import common.utils.RetryUtils;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.refresh;

@Getter
public class UserDashboard extends AuthorizedPage<UserDashboard> {
    public static final String WELCOME_TEXT = "Welcome, %s!";
    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createNewAccount() {
        return StepLogger.log("Create new account",
                () -> {
                    createNewAccount.click();
                    getAlert();
                    return this;
                }
        );
    }

    public UserDashboard checkWelcomeText(String name) {
        return StepLogger.log("Check welcome test has name = %s".formatted(name),
                () -> {
                    if (getWelcomeText().getText().equals(WELCOME_TEXT.formatted("noname"))) {
                        RetryUtils.retry("Check welcome text has name = %s".formatted(name),
                                () -> getWelcomeText().getText(),
                                text -> text.contains(name),
                                15,
                                1_000
                        );
                    }
                    getWelcomeText().shouldHave(Condition.text(
                            UserDashboard.WELCOME_TEXT.formatted(name)
                    ));
                    return this;
                }
        );
    }

}
