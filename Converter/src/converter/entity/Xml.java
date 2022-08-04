package converter.entity;

import converter.Element;

public class Xml implements Entity {
    private Element root;

    public Xml(Element root) {
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
