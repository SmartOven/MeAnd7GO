package converter;

import java.util.ArrayList;
import java.util.List;

public class Element {
    private final String name;
    private final String value;
    private final List<Attribute> attributes;
    private Element parent;
    private final List<Element> children;
    
    private boolean arrayElement = false;
    private boolean nullElement = false;

    public Element(String name, String value, List<Attribute> attributes, Element parent, List<Element> children) {
        this.name = name;
        this.value = value;
        this.attributes = (attributes != null) ? attributes : new ArrayList<>();
        this.parent = parent;
        this.children = (children != null) ? children : new ArrayList<>();
    }

    public Element(String name, String value, List<Attribute> attributes) {
        this(name, value, attributes, null, null);
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
        nullElement = false;
    }

    public void setNullElement(boolean nullElement) {
        this.nullElement = nullElement;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public boolean isArrayElement() {
        return arrayElement;
    }

    public boolean isNullElement() {
        return nullElement;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void setArrayElement(boolean arrayElement) {
        this.arrayElement = arrayElement;
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
