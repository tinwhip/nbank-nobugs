package testdataproviders;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static api.requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;

public class TransferDataProvider {

    public static Stream<Arguments> userCanNotTransferInvalidAmountBetweenAccountsSource() {
        return Stream.of(
                Arguments.of(0, "Transfer amount must be at least 0.01"),
                Arguments.of(-1, "Transfer amount must be at least 0.01"),
                Arguments.of(MAX_TRANSFER_AMOUNT + 1, "Transfer amount cannot exceed 10000")
        );
    }

}
