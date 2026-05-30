package constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NamePlaceholder {
    VALID_NAME_PLACEHOLDER("%s %s"),
    INVALID_NAME_PLACEHOLDER("%s %s %s");

    private final String placeholderValue;
}
