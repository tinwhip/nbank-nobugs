package ui.elements;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.pages.RepeatTransferModalWindow;

@Getter
public class Transaction extends BaseElement {
    private static final String REPEAT_BUTTON_NAME = "\uD83D\uDD01 Repeat";
    private final String type;
    private final double amount;
    private final String foundUnder;
    private final Button repeatButton;

    public Transaction(SelenideElement element) {
        super(element);

        String depositInfo = element.$("span").getText();
        type = depositInfo.split(" ")[0];
        amount = Double.parseDouble(depositInfo.split("\\$")[1].split("\n")[0]);
        foundUnder = getFoundUnder(depositInfo);
        repeatButton = new Button(element, REPEAT_BUTTON_NAME);
    }

    public RepeatTransferModalWindow openRepeatTransferWindow() {
        repeatButton.click();
        return Selenide.page(RepeatTransferModalWindow.class);
    }

    private String getFoundUnder(String depositInfo) {
        String[] foundUnderParts = depositInfo.split("Found under:");
        return foundUnderParts.length > 1 ? foundUnderParts[1].trim() : "";
    }

}
