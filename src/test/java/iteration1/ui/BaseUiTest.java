package iteration1.ui;

import com.codeborne.selenide.logevents.SelenideLogger;
import common.configs.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import common.extensions.BrowserMatchExtension;
import io.qameta.allure.selenide.AllureSelenide;
import iteration1.api.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(BrowserMatchExtension.class)
public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browserSize = Config.getProperty("uiBrowserSize");
        Configuration.browser = Config.getProperty("uiBrowser");
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
        //Configuration.headless = true;

        Configuration.browserCapabilities.setCapability(
                "selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @AfterEach
    public void teardown() {
        Selenide.closeWebDriver();
    }
}
