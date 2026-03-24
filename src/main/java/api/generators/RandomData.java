package api.generators;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class RandomData {
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

    public static int generateRandomAccountId() {
        return RandomUtils.nextInt(1000, 2000);
    }
}
