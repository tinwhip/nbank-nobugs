package ui.utils;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;

public final class ElementMapper {

    private ElementMapper() {}

    //ElementCollection -> List<BaseElement>
    public static  <T extends BaseElement> List<T> mapElements(ElementsCollection elementsCollection, Function<SelenideElement, T> constructor) {
        return elementsCollection.stream().map(constructor).toList();
    }

}
