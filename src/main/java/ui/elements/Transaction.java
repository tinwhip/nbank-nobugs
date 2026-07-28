package ui.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import lombok.Getter;
import ui.pages.RepeatTransferModalWindow;
import ui.pages.TransferAgainPage;

import static org.assertj.core.api.Assertions.assertThat;

@Getter
public class Transaction extends BaseElement {
    private static final String REPEAT_BUTTON_NAME = "\uD83D\uDD01 Repeat";
    private final String type;
    private final long relatedAccountId;
    private final double amount;
    private final Button repeatButton;

    public Transaction(SelenideElement element) {
        super(element);

        String depositInfo = element.$("span").getText();
        type = depositInfo.split(" ")[0];
        relatedAccountId = Long.parseLong(depositInfo.split("Related Account ID: ")[1]);
        amount = Double.parseDouble(depositInfo.split("\\$")[1].split("\n")[0]);
        repeatButton = new Button(element, REPEAT_BUTTON_NAME);
    }

    public RepeatTransferModalWindow openRepeatTransferWindow() {
        return StepLogger.log("Open repeat transfer window",
                () -> {
                    repeatButton.click();
                    return Selenide.page(RepeatTransferModalWindow.class);
                }
        );
    }

    public Transaction checkRelatedAccountId(long id) {
        return StepLogger.log("Check related account id = %d".formatted(id),
                () -> {
                    assertThat(relatedAccountId).isEqualTo(id);
                    return this;
                }
        );
    }
}
