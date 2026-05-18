package api.generators;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class RandomData {
    private static final String CYRILLIC_LETTERS =
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                    "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    private RandomData() {
    }

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase()
                + RandomStringUtils.randomAlphabetic(3).toLowerCase()
                + RandomStringUtils.randomNumeric(3)
                + "!%#@";
    }

    public static int getRandomAccountId() {
        return RandomUtils.nextInt(1000, 2000);
    }

    public static String getCyrillicString(int length) {
        return RandomStringUtils.random(length, CYRILLIC_LETTERS);
    }
}
