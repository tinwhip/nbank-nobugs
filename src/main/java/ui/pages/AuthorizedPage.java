package ui.pages;

import lombok.Getter;
import ui.elements.Header;

@Getter
public abstract class AuthorizedPage<T extends AuthorizedPage<T>> extends BasePage<T> {

    protected final Header header = new Header();

}
