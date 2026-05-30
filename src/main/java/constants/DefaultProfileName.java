package constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DefaultProfileName {
    DEFAULT_PROFILE_NAME("noname");

    private final String defaultNameValue;
}
