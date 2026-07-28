package ui.elements;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import common.helpers.StepLogger;

import static com.codeborne.selenide.Selenide.$;

public class Button extends BaseElement {

    public Button(String nameButton) {
        super($(Selectors.byTagAndText("button", nameButton)));
    }

    public Button(SelenideElement rootElement, String nameButton) {
        super(rootElement.$(Selectors.byTagAndText("button", nameButton)));
    }

    public Button click() {
        return StepLogger.log("Click button",
                () -> {
                    element.click();
                    return this;
                }
        );
    }

    public Button should(WebElementCondition condition) {
        return StepLogger.log("Button should be %s".formatted(condition.toString()),
                () -> {
                    element.should(condition);
                    return this;
                }
        );
    }

    public Button shouldNot(WebElementCondition condition) {
        return StepLogger.log("Button should not be %s".formatted(condition.toString()),
                () -> {
                    element.shouldNot(condition);
                    return this;
                }
        );
    }

}