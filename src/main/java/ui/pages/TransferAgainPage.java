package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import constants.TransferTypes;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.EnterInput;
import ui.elements.Transaction;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

@Getter
public class TransferAgainPage extends AuthorizedPage<TransferAgainPage> {

    private final EnterInput enterNameToFindTransactions = new EnterInput("name to find transactions");
    private final Button searchTransactionsButton = new Button("\uD83D\uDD0D Search Transactions");

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferAgainPage searchTransactions() {
        searchTransactionsButton.click();
        return this;
    }

    public TransferAgainPage searchTransactionsByUsernameOrName(String value) {
        enterNameToFindTransactions.clear();
        enterNameToFindTransactions.enter(value);
        searchTransactionsButton.click();
        return this;
    }

    public List<Transaction> getAllTransactions() {
        ElementsCollection elementsCollection = $(Selectors.byText("Matching Transactions")).parent().findAll("li");
        return generatePageElements(elementsCollection, Transaction::new);
    }

    public TransferAgainPage checkTransactionsContainsTypes(TransferTypes... transferTypes) {
        List<String> allTransactionTypes = getAllTransactions()
                .stream().map(Transaction::getType)
                .toList();

        assertThat(allTransactionTypes)
                .contains(
                        Arrays.stream(transferTypes)
                                .map(Enum::name)
                                .toArray(String[]::new)
                );
        return this;
    }

    public TransferAgainPage checkAllTransactionsHaveTypes(TransferTypes... transferTypes) {
        List<String> allTransactionTypes = getAllTransactions()
                .stream().map(Transaction::getType)
                .toList();

        assertThat(allTransactionTypes).isEqualTo(
                Arrays.stream(transferTypes)
                        .map(Enum::name)
                        .toList()
        );
        return this;
    }

    public TransferAgainPage checkAllTransactionsHaveAmount(double expectedAmount) {
        getAllTransactions().stream()
                .map(Transaction::getAmount)
                .forEach(amount -> assertThat(amount).isEqualTo(expectedAmount));
        return this;
    }

    public TransferAgainPage checkAllTransactionsHaveFoundUnder(String expectedFoundUnder) {
        getAllTransactions().stream()
                .map(Transaction::getFoundUnder)
                .forEach(foundUnder -> assertThat(foundUnder).isEqualTo(expectedFoundUnder));
        return this;
    }

    public TransferAgainPage checkTransactionsSize(int expectedSize) {
        assertThat(getAllTransactions().size()).isEqualTo(expectedSize);
        return this;
    }

    public Transaction goToTransactionByTransferType(TransferTypes transferType) {
        return getAllTransactions().stream()
                .filter(t -> t.getType().equals(transferType.name()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("%s transaction not found".formatted(transferType.name())));
    }

}
