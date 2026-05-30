package testdataproviders;

import java.util.stream.Stream;

import static api.generators.RandomData.*;
import static constants.NamePlaceholder.INVALID_NAME_PLACEHOLDER;
import static constants.NamePlaceholder.VALID_NAME_PLACEHOLDER;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public final class ChangeNameDataProvider {

    private ChangeNameDataProvider() {}

    public static Stream<String> validNameProvider() {
        return Stream.of(
                getCyrillicProfileName(),
                getLatinProfileName()
        );
    }

    public static Stream<String> invalidNameProvider() {
        return Stream.of(
                INVALID_NAME_PLACEHOLDER.getPlaceholderValue().formatted(
                        getCyrillicString(5), getCyrillicString(5), getCyrillicString(5)
                ),
                INVALID_NAME_PLACEHOLDER.getPlaceholderValue().formatted(
                        randomAlphabetic(5), randomAlphabetic(5), randomAlphabetic(5)
                ),
                VALID_NAME_PLACEHOLDER.getPlaceholderValue().formatted(
                        randomAlphanumeric(5),randomAlphanumeric(5)
                ),
                VALID_NAME_PLACEHOLDER.getPlaceholderValue().formatted(
                        randomNumeric(5), randomNumeric(5)
                )
        );
    }

}
