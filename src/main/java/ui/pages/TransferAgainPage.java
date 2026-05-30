package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import constants.TransferTypes;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.EnterInput;
import ui.elements.Transaction;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static constants.TransferTypes.TRANSFER_OUT;

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
        enterNameToFindTransactions.enter(value);
        searchTransactionsButton.click();
        return this;
    }

    public List<Transaction> getAllTransactions() {
        ElementsCollection elementsCollection = $(Selectors.byText("Matching Transactions")).parent().findAll("li");
        return generatePageElements(elementsCollection, Transaction::new);
    }

    public Transaction goToTransactionByTransferType(TransferTypes transferType) {
        return getAllTransactions().stream()
                .filter(t -> t.getType().equals(transferType.name()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("%s transaction not found".formatted(transferType.name())));
    }

}
