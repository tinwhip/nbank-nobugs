package ui.pages;

import com.codeborne.selenide.*;
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
        confirmTransferToAccountId.shouldHave(Condition.text(CONFIRM_TRANSFER_TEXT.formatted(accountId)));
        return this;
    }

    public TransferAgainPage repeatTransfer(Long senderAccountId, double amount) {
        accountSelector.selectAccount(senderAccountId);
        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));
        confirmDetailsButton.confirm();
        sendTransferButton.should(Condition.clickable).click();
        return Selenide.page(TransferAgainPage.class);
    }

    public RepeatTransferModalWindow selectAccount(Long accountId) {
        accountSelector.selectAccount(accountId);
        return this;
    }

    public RepeatTransferModalWindow enterAmount(double amount) {
        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));
        return this;
    }

    public RepeatTransferModalWindow confirmDetails() {
        confirmDetailsButton.confirm();
        return this;
    }

    public TransferAgainPage cancelTransfer() {
        cancelButton.click();
        return Selenide.page(TransferAgainPage.class);
    }
}
