package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.EnterInput;
import ui.elements.HomeButton;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class ProfilePage extends AuthorizedPage<ProfilePage> {

    private final SelenideElement titlePage = $(byText("✏\uFE0F Edit Profile"));
    private final EnterInput nameInput = new EnterInput("new name");
    private final Button saveChangesButton = new Button("\uD83D\uDCBE Save Changes");
    private final HomeButton homeButton = new HomeButton();

    @Override
    public String url() {
        return "/edit-profile";
    }

    public ProfilePage changeNameTo(String newName) {
        titlePage.shouldBe(visible);
        nameInput.enterWhenInputResetting(newName);
        saveChangesButton.click();
        return this;
    }

    public ProfilePage clear() {
        nameInput.clearWhenInputResetting();
        return this;
    }

    public ProfilePage saveChanges() {
        saveChangesButton.click();
        return this;
    }

}
