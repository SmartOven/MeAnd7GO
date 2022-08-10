package converter.entity;

import converter.Element;

public class Json implements Entity {
    private final Element root;

    public Json(Element root) {
        this.root = root;
    }

    @Override
    public Element getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
