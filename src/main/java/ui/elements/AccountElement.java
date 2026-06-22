package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class AccountElement extends BaseElement {
    private final String accountNumber;
    private double balance;
    private long id;

    public AccountElement(SelenideElement element) {
        super(element);
        String text = element.getText();
        accountNumber = text.split(" ")[0];
        balance = Double.parseDouble(
                text.split("\\$")[1].replace(")", "")
        );
        //id = Long.parseLong(accountNumber.replace("ACC", ""));
    }
}
