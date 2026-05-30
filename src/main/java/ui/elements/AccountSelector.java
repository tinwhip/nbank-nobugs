package ui.elements;

import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;

public class AccountSelector extends BaseElement {

    private static final String ALL_ACCOUNTS_SELECTOR = "option:not([value=''])";

    public AccountSelector() {
        super($("select[class*='account-selector']"));
    }

    public AccountSelector(SelenideElement rootElement) {
        super(rootElement.$("select"));
    }

    public AccountSelector selectAccount(Long accountId) {
        element.selectOptionByValue(accountId.toString());
        return this;
    }

    public List<AccountElement> getAllAccounts() {
        element.click();
        return generateElements(findAll(ALL_ACCOUNTS_SELECTOR), AccountElement::new);
    }

}
