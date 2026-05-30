package ui.elements;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ConfirmDetailsButton extends BaseElement{

    public ConfirmDetailsButton() {
        super($(By.id("confirmCheck")));
    }

    public void confirm() {
        element.click();
    }

}
