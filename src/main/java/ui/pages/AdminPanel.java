package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import common.utils.RetryUtils;
import lombok.Getter;
import ui.elements.UserBadge;

import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class AdminPanel extends AuthorizedPage<AdminPanel> {
    private SelenideElement adminPanelText = $(Selectors.byText("Admin Panel"));
    private SelenideElement addUserButton = $(Selectors.byText("Add User"));

    @Override
    public String url() {
        return "/admin";
    }

    public AdminPanel createUser(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        addUserButton.click();
        return this;
    }

    public List<UserBadge> getAllUsers() {
        return StepLogger.log(
                "Get all users from Dashboard",
                () -> {
                    ElementsCollection elementsCollection = $(Selectors.byText("All Users")).parent().findAll("li");
                    return generatePageElements(elementsCollection, UserBadge::new);
                }
        );
    }

    public UserBadge findUserByUsername(String username) {
        return RetryUtils.retry("Find user by username",
                () -> getAllUsers().stream().filter(it -> it.getUsername().equals(username))
                        .findAny().orElse(null),
                Objects::nonNull,
                10,
                3_000
        );
    }
}
