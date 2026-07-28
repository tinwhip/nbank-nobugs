package ui.pages;

import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.EnterInput;
import ui.elements.HomeButton;

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
        return StepLogger.log("Change name to = '%s'".formatted(newName),
                () -> {
                    titlePage.shouldBe(visible);
                    nameInput.enterWhenInputResetting(newName);
                    saveChangesButton.click();
                    return this;
                }
        );
    }

    public ProfilePage clear() {
        nameInput.clearWhenInputResetting();
        return this;
    }

    public ProfilePage saveChanges() {
        return StepLogger.log("Save changes",
                () -> {
                    saveChangesButton.click();
                    return this;
                }
        );
    }

}
