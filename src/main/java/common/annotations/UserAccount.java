package common.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface UserAccount {
    String name();
    int user();

    /**
     * Если amount > 0, после создания аккаунта пополняем его на эту сумму.
     */
    double amount() default 0;
}
