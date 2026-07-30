package ui.elements;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import common.helpers.StepLogger;
import ui.pages.BasePage;
import ui.pages.ProfilePage;

import static com.codeborne.selenide.Selenide.$;

public class HomeButton extends BaseElement {

    public HomeButton() {
        super($(Selectors.byTagAndText("button", "\uD83C\uDFE0 Home")));
    }

    public <P extends BasePage<P>> P goHome(Class<P> homePage) {
        return StepLogger.log("Go home",
                () -> {
                    element.click();
                    return Selenide.page(homePage);
                }
        );
    }

}
