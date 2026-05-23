package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.AccountElement;

import java.util.List;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class DepositMoney extends AuthorizedPage<DepositMoney> {
    private SelenideElement depositMoneyText = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private SelenideElement selectorAccount = $("select[class*='account-selector']");
    private SelenideElement amountInput = $(Selectors.byPlaceholder("Enter amount"));
    private SelenideElement depositButton = $(Selectors.byTagAndText("button", "\uD83D\uDCB5 Deposit"));
    private SelenideElement homeButton = $(Selectors.byTagAndText("button", "\uD83C\uDFE0 Home"));

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositMoney depositMoneyToAccount(Long accountId, double amount) {
        selectorAccount.selectOptionByValue(accountId.toString());
        amountInput.sendKeys(String.valueOf(amount));
        depositButton.click();
        return this;
    }

    public List<AccountElement> getAllAccounts() {
        selectorAccount.click();
        ElementsCollection accounts = $$("select.account-selector option:not([value=''])");
        return generatePageElements(accounts, AccountElement::new);
    }
}
