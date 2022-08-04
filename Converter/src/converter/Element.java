package converter;

import java.util.ArrayList;
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
        this.attributes = (attributes != null) ? attributes : new ArrayList<>();
        this.parent = parent;
        this.children = (children != null) ? children : new ArrayList<>();
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

    public void addChild(Element child) {
        children.add(child);
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public boolean isEmptyArrayElement() {
        return value != null && value.isEmpty();
    }

    public boolean isNullElement() {
        return value == null && !hasChildren();
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public void markAsEmptyArray() {
        this.value = "";
    }

    public record Attribute(String name, String value) {

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name + " = \"" + value + "\"";
        }
    }
}
