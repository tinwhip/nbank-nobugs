package constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@RequiredArgsConstructor
@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New AccountAnnotation Created! AccountAnnotation Number: "),

    DEPOSIT_SUCCESSFULLY("✅ Successfully deposited $%.1f to account %s!"),
    ENTER_A_VALID_AMOUNT("❌ Please enter a valid amount."),
    DEPOSIT_LESS_OR_EQUAL_TO("❌ Please deposit less or equal to 5000$."),
    PLEASE_SELECT_ACCOUNT("❌ Please select an account."),

    TRANSFER_SUCCESSFULLY("✅ Successfully transferred $%.1f to account %s!"),
    INVALID_TRANSFER("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER("❌ No user found with this account number."),

    FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),

    SUCCESSFUL_REPEAT_TRANSFER("✅ Transfer of $%.1f successful from Account %d to %d!"),
    INVALID_REPEAT_TRANSFER("❌ Transfer failed: Please try again."),

    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_IS_THE_SAME("⚠\uFE0F New name is the same as the current one."),
    ENTER_A_VALID_NAME("❌ Please enter a valid name.");

    private final String message;

    public static String getFormattedMessageWithDouble(BankAlert alert, Object... arguments) {
        return String.format(
                Locale.US,
                alert.getMessage(),
                arguments
        );
    }
}