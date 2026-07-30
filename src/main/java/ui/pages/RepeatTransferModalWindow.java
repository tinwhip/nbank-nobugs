package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import lombok.Getter;
import org.openqa.selenium.By;
import ui.elements.AccountSelector;
import ui.elements.BaseElement;
import ui.elements.Button;
import ui.elements.ConfirmDetailsButton;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class RepeatTransferModalWindow extends BaseElement {

    private final SelenideElement confirmTransferToAccountId = element.find(Selectors.byTagName("p"));
    private final AccountSelector accountSelector = new AccountSelector(element);
    private final SelenideElement amountInput = element.find("input[type='number']");
    private final ConfirmDetailsButton confirmDetailsButton = new ConfirmDetailsButton();
    private final Button cancelButton = new Button(element, "Cancel");
    private final Button sendTransferButton = new Button(element, "\uD83D\uDE80 Send Transfer");

    private static final String CONFIRM_TRANSFER_TEXT = "Confirm transfer to Account ID: %d";

    public RepeatTransferModalWindow() {
        super($(By.className("modal-content")));
    }

    public RepeatTransferModalWindow checkTransferToAccountId(Long accountId) {
        return StepLogger.log("Check transfer to account with id = %d".formatted(accountId),
                () -> {
                    confirmTransferToAccountId.shouldHave(Condition.text(CONFIRM_TRANSFER_TEXT.formatted(accountId)));
                    return this;
                }
        );
    }

    public TransferAgainPage repeatTransfer(Long senderAccountId, double amount) {
        return StepLogger.log(
                "Repeat transfer (amount = %f) to account with sender account id = %d"
                        .formatted(amount, senderAccountId),
                () -> {
                    accountSelector.selectAccount(senderAccountId);
                    amountInput.clear();
                    amountInput.sendKeys(String.valueOf(amount));
                    confirmDetailsButton.confirm();
                    sendTransferButton.should(Condition.clickable).click();
                    return Selenide.page(TransferAgainPage.class);
                }
        );
    }

    public RepeatTransferModalWindow selectAccount(Long accountId) {
        return StepLogger.log("Select account with id = %d".formatted(accountId),
                () -> {
                    accountSelector.selectAccount(accountId);
                    return this;
                }
        );
    }

    public RepeatTransferModalWindow enterAmount(double amount) {
        return StepLogger.log("Enter amount = %f".formatted(amount),
                () -> {
                    amountInput.clear();
                    amountInput.sendKeys(String.valueOf(amount));
                    return this;
                }
        );
    }

    public RepeatTransferModalWindow confirmDetails() {
        return StepLogger.log("Confirm details",
                () -> {
                    confirmDetailsButton.confirm();
                    return this;
                }
        );
    }

    public TransferAgainPage cancelTransfer() {
        return StepLogger.log("Cancel transfer",
                () -> {
                    cancelButton.click();
                    return Selenide.page(TransferAgainPage.class);
                }
        );
    }
}
