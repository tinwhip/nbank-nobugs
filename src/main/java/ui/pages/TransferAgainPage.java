package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import common.helpers.StepLogger;
import common.utils.RetryUtils;
import constants.TransferTypes;
import lombok.Getter;
import ui.elements.AccountSelector;
import ui.elements.Button;
import ui.elements.EnterInput;
import ui.elements.Transaction;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

@Getter
public class TransferAgainPage extends AuthorizedPage<TransferAgainPage> {

    private final EnterInput enterNameToFindTransactions = new EnterInput("name to find transactions");
    private final Button searchTransactionsButton = new Button("\uD83D\uDD0D Search Transactions");
    private final AccountSelector accountSelector = new AccountSelector();

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferAgainPage selectAccount(Long accountId) {
        return StepLogger.log("Select account with id = %d".formatted(accountId),
                () -> {
                    accountSelector.selectAccount(accountId);
                    return this;
                }
        );
    }

    public List<Transaction> getAllTransactions() {
        return StepLogger.log("Get all transactions",
                () -> {
                    ElementsCollection elementsCollection = $$("ul.list-group li");
                    return generatePageElements(elementsCollection, Transaction::new);
                }
        );
    }

    public TransferAgainPage checkTransactionsContainsTypes(TransferTypes... transferTypes) {
        return StepLogger.log("Check transactions contain types %s".formatted(Arrays.toString(transferTypes)),
                () -> {
                    String[] array = Arrays.stream(transferTypes)
                            .map(Enum::name)
                            .toArray(String[]::new);

                    List<String> allTransactions = RetryUtils.retry("getAllTransactions",
                            () -> getAllTransactions()
                                    .stream().map(Transaction::getType)
                                    .toList(),
                            transactions -> transactions.contains(array[0]),
                            10,
                            1_000
                    );


                    assertThat(allTransactions)
                            .contains(
                                    array
                            );
                    return this;
                }
        );
    }

    public Transaction goToTransaction(TransferTypes transferType) {
        return StepLogger.log("Go to transaction with type = '%s'".formatted(transferType.name()),
                () -> getAllTransactions().stream()
                        .filter(transaction -> transaction.getType().equals(transferType.name()))
                        .findFirst().orElseThrow()
        );
    }

    public TransferAgainPage checkAllTransactionsHaveAmount(double expectedAmount) {
        return StepLogger.log("Check all transactions have amount = %f".formatted(expectedAmount),
                () -> {
                    getAllTransactions().stream()
                            .map(Transaction::getAmount)
                            .forEach(amount -> assertThat(amount).isEqualTo(expectedAmount));
                    return this;
                }
        );
    }

    public Transaction goToTransactionByTransferType(TransferTypes transferType) {
        return RetryUtils.retry("Go to transaction with type = %s".formatted(transferType.name()),
                () -> getAllTransactions().stream()
                        .filter(t -> t.getType().equals(transferType.name()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("%s transaction not found".formatted(transferType.name())))
                ,
                transaction -> transaction != null,
                10,
                1_000);
    }

}
