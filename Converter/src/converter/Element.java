package converter;

import java.util.ArrayList;
import java.util.List;

public class Element {
    private final String name;
    private String value;
    private final List<Attribute> attributes;
    private Element parent;
    private final List<Element> children;
    
    private boolean containArray = false;
    private boolean nullValue = false;

    public static Element emptyArray(String name, List<Attribute> attributes) {
        Element element = new Element(name, null, attributes);
        element.setContainArray(true);
        return element;
    }

    public static Element nullElement(String name, List<Attribute> attributes) {
        Element element = new Element(name, null, attributes);
        element.setNullValue(true);
        return element;
    }

    public static Element arrayElement(String value, Element parent) {
        return new Element("element", value, null, parent, null);
    }

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

    public void setValue(String value) {
        this.value = value;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public Element getParent() {
        return parent;
    }

    public List<Element> getChildren() {
        return children;
    }

    public void addChild(Element child) {
        children.add(child);
        nullValue = false;
    }

    public void setNullValue(boolean nullValue) {
        this.nullValue = nullValue;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public boolean isContainArray() {
        return containArray;
    }

    public boolean isNullValue() {
        return nullValue;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void setContainArray(boolean containArray) {
        this.containArray = containArray;
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
