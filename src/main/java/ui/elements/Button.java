package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;

import static com.codeborne.selenide.Selenide.$;

public class Button extends BaseElement {

    public Button(String nameButton) {
        super($(Selectors.byTagAndText("button", nameButton)));
    }

    public Button(SelenideElement rootElement, String nameButton) {
        super(rootElement.$(Selectors.byTagAndText("button", nameButton)));
    }

    public Button click() {
        element.click();
        return this;
    }

    public Button should(WebElementCondition condition) {
        element.should(condition);
        return this;
    }

    public Button shouldNot(WebElementCondition condition) {
        element.shouldNot(condition);
        return this;
    }

}