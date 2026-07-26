package ui.pages;

import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.utils.RetryUtils;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThat;
import static ui.utils.ElementMapper.mapElements;

public abstract class BasePage<T extends BasePage<T>> {
    protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));

    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlertMessageAndAccept(String bankAlert) {
        Alert alert = getAlert();
        assertThat(alert.getText()).contains(bankAlert);
        alert.accept();

        return (T) this;
    }

    public static Alert getAlert() {
        return RetryUtils.retry("Get alert",
                () -> {
                    try {
                        return switchTo().alert(Duration.ofSeconds(3));
                    } catch (Throwable e) {
                        return null;
                    }
                },
                Objects::nonNull,
                10,
                4_000
        );
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public static void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }

    protected <T extends BaseElement> List<T> generatePageElements(
            ElementsCollection elementsCollection, Function<SelenideElement, T> constructor
    ) {
        return mapElements(elementsCollection, constructor);
    }
}
