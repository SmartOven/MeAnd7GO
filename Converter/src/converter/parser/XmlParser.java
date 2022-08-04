package converter.parser;

import converter.Element;
import converter.entity.Xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser implements Parser<Xml> {

    // Regex patterns
    // XML tags
    private static final Pattern xmlTagPattern;
    private static final Pattern xmlOpenTagPattern;
    private static final Pattern xmlCloseTagPattern;
    private static final Pattern xmlSelfCloseTagPattern;
    private static final Pattern xmlConfigTagPattern;

    static {
        xmlTagPattern = Pattern.compile("</?\\s*[a-z0-9_\\-]+[^>]+?/?>");
        xmlOpenTagPattern = Pattern.compile("<\\s*[a-z0-9_\\-]+(\\s+[a-z0-9_\\-]+\\s*=\\s*\"[^\"]*\")*\\s*>");
        xmlCloseTagPattern = Pattern.compile("</\\s*[a-z0-9_\\-]+\\s*>");
        xmlSelfCloseTagPattern = Pattern.compile("<\\s*[a-z0-9_\\-]+\\s*/>");
        xmlConfigTagPattern = Pattern.compile("<\\?.+?\\?>");
    }

    @Override
    public Xml parse(String data) {
        // Skip config tags
        int start = 0;
        Matcher configTagsMatcher = xmlConfigTagPattern.matcher(data);
        while (configTagsMatcher.find()) {
            start = configTagsMatcher.end();
        }
        data = data.substring(start);

        Stack<Element> elementStack = new Stack<>();
        Element root = null;
        Matcher dataMather = xmlTagPattern.matcher(data);
        // Scan every xml tag
        while (dataMather.find()) {
            String tag = dataMather.group();
            String noBreaksTag = removeTagBreaks(tag);

            Element element = new Element(
                    getName(noBreaksTag),
                    getValue(data, dataMather.end()),
                    getAttributes(noBreaksTag),
                    null,
                    new ArrayList<>()
            );

            if (isOpen(tag)) {
                // Resolving parent
                resolveParent(element, elementStack);

                // Add tag to the stack
                elementStack.add(element);
            } else if (isClose(tag)) {
                // Remove tag from the stack (if it equals prev name)
                if (!isClosingOpenedTag(element, elementStack)) {
                    throw new IllegalArgumentException();
                }
                root = elementStack.pop();
            } else if (isSelfClose(tag)) {
                // Resolving parent
                resolveParent(element, elementStack);

                // No need to track in the stack
            } else {
                throw new IllegalArgumentException();
            }
        }
        return new Xml(root);
    }

    private void resolveParent(Element element, Stack<Element> elementStack) {
        // Add element as child and set it parent
        if (!elementStack.isEmpty()) {
            Element parent = elementStack.peek();
            parent.addChild(element);
            element.setParent(parent);
        }
    }

    private boolean isClosingOpenedTag(Element element, Stack<Element> elementStack) {
        return !elementStack.isEmpty() && element.getName().equals(elementStack.peek().getName());
    }

    private boolean isOpen(String tag) {
        return xmlOpenTagPattern.matcher(tag).matches();
    }

    private boolean isClose(String tag) {
        return xmlCloseTagPattern.matcher(tag).matches();
    }

    private boolean isSelfClose(String tag) {
        return xmlSelfCloseTagPattern.matcher(tag).matches();
    }

    private List<Element.Attribute> getAttributes(String noBreaksTag) {
        // Remove tag name from noBreaksTag
        noBreaksTag = noBreaksTag.replaceAll("^[^\\s]*\\s*", "");
        Matcher attributeMatcher = Pattern.compile("[a-z0-9_\\-]+\\s*=\\s*\"[^\"]+\"").matcher(noBreaksTag);
        List<Element.Attribute> attributes = new ArrayList<>();
        while (attributeMatcher.find()){
            String[] attribute = attributeMatcher.group().split("\\s*=\\s*");
            if (attribute.length != 2) {
                throw new IllegalArgumentException();
            }
            attributes.add(new Element.Attribute(attribute[0], attribute[1].replaceAll("\"", "")));
        }
        return attributes;
    }

    private String getValue(String data, int start) {
        // Remove whitespaces in the front and find value
        data = data.substring(start).replaceAll("^\\s*", "");
        Matcher valueMatcher = Pattern.compile("[^<]*").matcher(data);
        if (!valueMatcher.find()) {
            throw new IllegalArgumentException();
        }
        String value = valueMatcher.group();
        return "".equals(value) ? null : value;
    }

    private String getName(String noBreaksTag) {
        Matcher matcher = Pattern.compile("^[a-z0-9_\\-]+").matcher(noBreaksTag);
        if (!matcher.find()) {
            throw new IllegalArgumentException();
        }
        return matcher.group();
    }

    private String removeTagBreaks(String tag) {
        tag = tag.replaceAll("</?\\s*", "");
        tag = tag.replaceAll("\\s*/?>", "");
        return tag;
    }

    public static void main(String[] args) {
        String data = """
                <?xml version = "1.0" encoding = "utf-8"?>
                <transactions attr1="value1" attr2 = "value2" >
                    <id>6753322</id>
                    <data>
                        <element>123</element>
                        <element>true</element>
                        <element>false</element>
                        <element></element>
                        <element></element>
                        <element></element>
                        <element></element>
                        <element>
                            <element>1</element>
                            <element>2</element>
                            <element>3</element>
                            <element attr="value6">value7</element>
                        </element>
                        <element />
                        <element></element>
                        <element>
                            <key1>value1</key1>
                            <key2 attr="value2">value3</key2>
                        </element>
                        <element attr2="value4">value5</element>
                    </data>
                </transactions>
                """;

        var obj = new XmlParser().parse(data);
    }
}
