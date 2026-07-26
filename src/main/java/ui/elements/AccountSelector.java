package ui.elements;

import com.codeborne.selenide.SelenideElement;
import common.utils.RetryUtils;

import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
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
        RetryUtils.retry("Select account",
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
        double actualAmount = RetryUtils.retry(
                "Проверка баланса счёта %s".formatted(accountNumber),
                () -> getAllAccounts().stream()
                        .filter(account -> account.getAccountNumber().equals(accountNumber))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException(
                                "Account number %s not found".formatted(accountNumber)
                        ))
                        .getBalance(),
                balance -> Double.compare(balance, amount) == 0,
                10,
                1_000
        );

        assertThat(actualAmount)
                .as("Баланс счёта %s".formatted(accountNumber))
                .isEqualTo(amount);

        return this;
    }

}
