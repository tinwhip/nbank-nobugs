package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import common.utils.RetryUtils;
import lombok.Getter;
import org.awaitility.Awaitility;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.Keys.chord;

public class EnterInput extends BaseElement {

    public EnterInput(String enter) {
        super($(Selectors.byPlaceholder("Enter %s".formatted(enter))));
    }

    public EnterInput enter(String value) {
        element.shouldBe(visible, enabled, interactable);
        element.sendKeys(value);
        element.shouldHave(value(value));
        element.pressTab();
        return this;
    }

    public EnterInput enterWhenInputResetting(String value) {
        element.shouldBe(visible, enabled, interactable);
        element.shouldNotBe(readonly);

        RetryUtils.retry(
                () -> {
                    clear();
                    element.sendKeys(value);
                    Selenide.sleep(100);
                    return element.getValue();
                },
                expValue -> value.equals(expValue),
                10,
                1_000
        );
        element.shouldHave(value(value));

        return this;
    }

    public EnterInput clear() {
        element.sendKeys(chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.BACK_SPACE);
        return this;
    }

    public EnterInput clearWhenInputResetting() {
        element.shouldBe(visible, enabled, interactable)
                .shouldNotBe(readonly);

        RetryUtils.retry(
                () -> {
                    clear();
                    Selenide.sleep(100);
                    return element.getValue();
                },
                value -> Objects.equals("", value),
                10,
                1_000
        );

        element.shouldBe(empty);

        return this;
    }
}
