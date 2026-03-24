package iteration1.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.LoginUserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import api.Endpoint;
import api.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.66:3000";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";

        Configuration.browserCapabilities.setCapability(
                "selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest user = AdminSteps.createUser();
        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        )
                .post(new LoginUserRequest(user.getUsername(), user.getPassword()))
                .extract()
                .header("Authorization");

        Selenide.open("/");

        Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        Selenide.open("/dashboard");

        $(Selectors.byText("➕ Create New Account")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("✅ New Account Created! Account Number:");
        alert.accept();

        Pattern pattern = Pattern.compile("Account Number: (\\w+)");
        Matcher matcher = pattern.matcher(alertText);
        matcher.find();
        String createdAccNumber = matcher.group(1);

        //аккаунт был создан на API
        CreateAccountResponse[] existingUserAccounts = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()
        ).get().statusCode(SC_OK)
                .extract().as(CreateAccountResponse[].class);
        assertThat(existingUserAccounts).hasSize(1);

        CreateAccountResponse createdAccount = existingUserAccounts[0];
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();
    }
}
