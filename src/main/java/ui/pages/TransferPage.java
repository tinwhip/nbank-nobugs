package ui.pages;

import com.codeborne.selenide.Selenide;
import common.utils.RetryUtils;
import lombok.Getter;
import ui.elements.*;

import static com.codeborne.selenide.Condition.clickable;
import static constants.BankAlert.USERS_LIST_IS_NOT_LOADED;

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
        sendTransferButton.should(clickable).click();
        if (getAlert().getText().equals(USERS_LIST_IS_NOT_LOADED.getMessage())) {
            RetryUtils.retry("Send Transfer Button",
                    () -> {
                        getAlert().accept();
                        sendTransferButton.should(clickable).click();
                        return getAlert().getText();
                    },
                    alert -> !alert.equals(USERS_LIST_IS_NOT_LOADED.getMessage()),
                    5,
                    3_000);
        }
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
        sendTransfer();
        return this;
    }

    public TransferAgainPage transferAgain() {
        transferAgainButton.click();
        return Selenide.page(TransferAgainPage.class);
    }

}
