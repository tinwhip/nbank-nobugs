package iteration1.ui;

import com.codeborne.selenide.*;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import api.Endpoint;
import api.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Arrays;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest {

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
    public void adminCanCreateUserTest() {
        //админ залогинился в банке
        CreateUserRequest admin = CreateUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        //админ создает юзера в банке
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.getPassword());
        $(Selectors.byText("Add User")).click();

        //проверка, что алерт "User created successfully!"
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).isEqualTo("✅ User created successfully!");
        alert.accept();

        //проверка, что юзер отображается на UI

        ElementsCollection allUsersFromDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsersFromDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);

        //проверка, что юзер создан на API
        CreateUserResponse[] users = new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOK()
        ).get().statusCode(SC_OK)
                .extract().as(CreateUserResponse[].class);

        CreateUserResponse createdUser = Arrays.stream(users)
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();
        ModelAssertions.assertThatModels(newUser, createdUser).match();
    }

    @Test
    public void adminCannotCreateUserWithInvalidDataTest() {
        //админ залогинился в банке
        CreateUserRequest admin = CreateUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        //админ создает юзера в банке
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);
        newUser.setUsername("a");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.getPassword());
        $(Selectors.byText("Add User")).click();

        //проверка, что алерт "Failed to create user:"
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("Username must be between 3 and 15 characters");
        alert.accept();

        //проверка, что юзер отображается на UI

        ElementsCollection allUsersFromDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsersFromDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);

        //проверка, что юзер создан на API
        CreateUserResponse[] users = new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOK()
        ).get().statusCode(SC_OK)
                .extract().as(CreateUserResponse[].class);

        long usersWithSameUsernameAsNewUser = Arrays.stream(users)
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .count();
        assertThat(usersWithSameUsernameAsNewUser).isZero();
    }
}
