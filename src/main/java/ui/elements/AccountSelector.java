package ui.elements;

import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

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

    public AccountSelector checkAccountHasBalance(Long accountId, double amount) {
        assertThat(
                getAllAccounts().stream()
                        .filter(account -> account.getId() == accountId)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Account id %d not found".formatted(accountId)))
                        .getBalance()
        ).isEqualTo(amount);
        return this;
    }

}
