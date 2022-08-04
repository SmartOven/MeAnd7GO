package converter;

import java.util.List;

public class Element {
    private String name;
    private String value;
    private List<Attribute> attributes;
    private Element parent;
    private List<Element> children;

    public Element(String name, String value, List<Attribute> attributes, Element parent, List<Element> children) {
        this.name = name;
        this.value = value;
        this.attributes = attributes;
        this.parent = parent;
        this.children = children;
    }

    public boolean isValueNull() {
        return value == null;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Element getParent() {
        return parent;
    }

    public List<Element> getChildren() {
        return children;
    }

    private static class Attribute {

    }
}
