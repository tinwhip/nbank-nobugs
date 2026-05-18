package constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AdminCredentials {
    CREDENTIALS("admin", "admin");
    private final String username;
    private final String password;
}
