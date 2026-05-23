package constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New AccountAnnotation Created! AccountAnnotation Number: "),

    DEPOSIT_SUCCESSFULLY("✅ Successfully deposited $%.1f to account %s!"),
    ENTER_A_VALID_AMOUNT("❌ Please enter a valid amount."),
    DEPOSIT_LESS_OR_EQUAL_TO("❌ Please deposit less or equal to 5000$."),
    PLEASE_SELECT_ACCOUNT("❌ Please select an account.");

    private final String message;
}