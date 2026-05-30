package api.generators;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import static constants.NamePlaceholder.VALID_NAME_PLACEHOLDER;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

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

    public static String getRandomAccountNumber() {
        return "ACC" + getRandomAccountId();
    }

    public static String getCyrillicString(int length) {
        return RandomStringUtils.random(length, CYRILLIC_LETTERS);
    }

    public static double getRandomDouble(double from, double to) {
        return getRandomDouble(from, to, 1);
    }

    public static double getRandomDouble(double from, double to, int scale) {
        double value = RandomUtils.nextDouble(from, to);
        return round(value, scale);
    }

    public static double round(double value, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("scale must be >= 0");
        }

        double factor = Math.pow(10, scale);
        return Math.round(value * factor) / factor;
    }

    public static String getCyrillicProfileName() {
        return VALID_NAME_PLACEHOLDER.getPlaceholderValue()
                .formatted(getCyrillicString(5), getCyrillicString(5));
    }

    public static String getLatinProfileName() {
        return VALID_NAME_PLACEHOLDER.getPlaceholderValue()
                .formatted(randomAlphabetic(5), randomAlphabetic(5));
    }

}
