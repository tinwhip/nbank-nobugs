package testdataproviders;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static api.requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;
import static constants.ResponseMessage.INVALID_TRANSFER;
import static constants.ResponseMessage.TRANSFER_AMOUNT_CANNOT_EXCEED;

public class TransferDataProvider {

    public static Stream<Arguments> userCanNotTransferInvalidAmountBetweenAccountsSource() {
        return Stream.of(
                //Arguments.of(0, INVALID_TRANSFER.getMessage()),
                Arguments.of(-1, INVALID_TRANSFER.getMessage()),
                Arguments.of(MAX_TRANSFER_AMOUNT + 1, TRANSFER_AMOUNT_CANNOT_EXCEED.getMessage())
        );
    }

}
