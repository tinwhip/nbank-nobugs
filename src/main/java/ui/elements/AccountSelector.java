package ui.elements;

import com.codeborne.selenide.SelenideElement;
import common.utils.RetryUtils;

import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
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
        String accountValue = accountId.toString();
        RetryUtils.retry(
                () -> {
                    try {
                        element.shouldBe(visible, enabled);
                        element.selectOptionByValue(accountValue);
                        return element.getSelectedOptionValue();
                    } catch (Throwable e) {
                        return null;
                    }
                },
                selectedValue -> Objects.equals(accountValue, selectedValue),
                10,
                3000
        );
        return this;
    }

    public List<AccountElement> getAllAccounts() {
        element.click();
        return generateElements(findAll(ALL_ACCOUNTS_SELECTOR), AccountElement::new);
    }

    public AccountSelector checkAccountHasBalance(String accountNumber, double amount) {
        assertThat(
                getAllAccounts().stream()
                        .filter(account -> account.getAccountNumber().equals(accountNumber))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Account number %s not found".formatted(accountNumber)))
                        .getBalance()
        ).isEqualTo(amount);
        return this;
    }

}
