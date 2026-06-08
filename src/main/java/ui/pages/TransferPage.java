package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;
import ui.elements.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class TransferPage extends AuthorizedPage<TransferPage> {

    private final Button newTransferButton = new Button("\uD83C\uDD95 New Transfer");
    private final Button transferAgainButton = new Button("\uD83D\uDD01 Transfer Again");
    private final AccountSelector accountSelector = new AccountSelector();
    private final EnterInput recipientNameInput = new EnterInput("recipient name");
    private final EnterInput recipientAccountNumberInput = new EnterInput("recipient account number");
    private final EnterInput enterAmount = new EnterInput("amount");
    private final ConfirmDetailsButton confirmDetailsButton = new ConfirmDetailsButton();
    private final HomeButton homeButton = new HomeButton();
    private final Button sendTransferButton = new Button("\uD83D\uDE80 Send Transfer");

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage selectAccount(Long accountId) {
        accountSelector.selectAccount(accountId);
        return this;
    }

    public TransferPage enterRecipientName(String recipientName) {
        recipientNameInput.enter(recipientName);
        return this;
    }

    public TransferPage enterRecipientAccountNumber(String recipientAccountNumber) {
        recipientAccountNumberInput.enter(recipientAccountNumber);
        return this;
    }

    public TransferPage enterAmount(double amount) {
        enterAmount.enter(String.valueOf(amount));
        return this;
    }

    public TransferPage confirmDetails() {
        confirmDetailsButton.confirm();
        return this;
    }

    public TransferPage sendTransfer() {
        sendTransferButton.click();
        return this;
    }

    public TransferPage sendTransfer(
            Long accountId,
            String recipientName,
            String recipientAccountNumber,
            double amount
    ) {
        accountSelector.selectAccount(accountId);
        recipientNameInput.enter(recipientName);
        recipientAccountNumberInput.enter(recipientAccountNumber);
        enterAmount.enter(String.valueOf(amount));
        confirmDetailsButton.confirm();
        sendTransferButton.should(clickable).click();
        return this;
    }

    public TransferAgainPage transferAgain() {
        transferAgainButton.click();
        return Selenide.page(TransferAgainPage.class);
    }

}
