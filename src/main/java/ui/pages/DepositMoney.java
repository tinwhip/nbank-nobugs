package ui.pages;

import api.specs.RequestSpecs;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import lombok.Getter;
import ui.elements.AccountSelector;
import ui.elements.Button;
import ui.elements.EnterInput;
import ui.elements.HomeButton;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class DepositMoney extends AuthorizedPage<DepositMoney> {
    private final SelenideElement depositMoneyText = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private final AccountSelector accountSelector = new AccountSelector();
    private final HomeButton homeButton = new HomeButton();
    private final EnterInput amountInput = new EnterInput("amount");
    private final Button depositButton = new Button("\uD83D\uDCB5 Deposit");

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositMoney depositMoneyToAccount(Long accountId, double amount) {
        return StepLogger.log("Deposit money (amount = %d) to account %d".formatted(amount, accountId),
                () -> {
                    accountSelector.selectAccount(accountId);
                    amountInput.enter(String.valueOf(amount));
                    depositButton.click();
                    return this;
                }
        );

    }

}
