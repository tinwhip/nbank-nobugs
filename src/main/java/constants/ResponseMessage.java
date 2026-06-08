package constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseMessage {
    BLANK_USERNAME("Username cannot be blank"),
    USERNAME_MUST_BE_BETWEEN("Username must be between 3 and 15 characters"),
    USERNAME_MUST_CONTAIN_ONLY("Username must contain only letters, digits, dashes, underscores, and dots"),
    BLANK_PASSWORD("Password cannot be blank"),
    PASSWORD_MUST_CONTAIN("Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),

    UNAUTH_ACCESS_TO_ACCOUNT("Unauthorized access to account"),
    INVALID_ACCOUNT_OR_AMOUNT("Invalid account or amount"),

    TRANSFER_SUCCESSFUL("Transfer successful"),
    INVALID_TRANSFER("Invalid transfer: insufficient funds or invalid accounts"),

    PROFILE_UPDATED_SUCCESSFULLY("Profile updated successfully"),
    NAME_MUST_CONTAIN_TWO_WORDS("Name must contain two words with letters only"),

    DEPOSIT_AMOUNT_MUST_BE_AT_LEAST("Deposit amount must be at least 0.01"),
    DEPOSIT_AMOUNT_CANNOT_EXCEED("Deposit amount cannot exceed 5000");

    private final String message;
}
