package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
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

        Awaitility.await()
                .pollInSameThread()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> {
                    element.clear();
                    element.sendKeys(value);
                    Selenide.sleep(10);
                    return value.equals(element.getValue());
                });

        element.shouldHave(value(value));

        return this;
    }

    public EnterInput clear() {
        element.sendKeys(chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.BACK_SPACE);
        return this;
    }

    public EnterInput clearWhenInputResetting() {
        element
                .shouldBe(visible)
                .shouldBe(enabled)
                .shouldBe(interactable)
                .shouldNotBe(readonly);

        Awaitility.await()
                .pollInSameThread()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> {
                    clear();

                    Selenide.sleep(10);

                    return Objects.equals("", element.getValue());
                });

        element.shouldBe(empty);

        return this;
    }
}
