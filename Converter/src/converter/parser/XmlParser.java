package converter.parser;

import converter.Element;
import converter.entity.Xml;
import converter.error.XmlSyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser implements Parser<Xml> {

    // Regex patterns for XML tags
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

    /**
     * <h3>Parsing Xml file data trough Xml tags:</h3>
     * <p>Skipping config tag</p>
     * <p>For the remaining tags:</p>
     * <ol>
     *     <li><b>If it is open tag</b>: add new element to the stack (prev stack element is the parent for the new one)</li>
     *     <li><b>If it is close tag</b>: remove last element from the stack (if closing tag name is not equal to the opening tag name - throw error)</li>
     *     <li><b>If it is self-closing tag</b>: don't touch stack, the last stack element is parent as in case with opening tag</li>
     * </ol>
     * @param data String with Xml file data
     * @return Xml object
     */
    @Override
    public Xml parse(String data) {
        // Skip config tags
        data = skipConfigTag(data);

        Stack<Element> elementStack = new Stack<>();  // tracking parents for tags
        Element root = null;  // tracking root element
        Matcher dataMather = xmlTagPattern.matcher(data);

        // Scan every xml tag
        while (dataMather.find()) {
            String tag = dataMather.group();
            String noBreaksTag = removeTagBreaks(tag);

            if (isOpen(tag)) {
                // Create element object
                Element element = new Element(
                        getName(noBreaksTag),
                        getValue(data, dataMather.end()),
                        getAttributes(noBreaksTag)
                );

                // Adding element as child for current parent
                addAsChildToParent(element, elementStack);

                // If tag is opening and has no value - it is an array
                element.setContainArray(element.getValue() == null);

                // Add tag to the stack
                elementStack.add(element);
            } else if (isClose(tag)) {
                String closeTagName = getName(noBreaksTag);

                // Remove tag from the stack (if it is closing previously opened tag)
                if (!isClosingOpenedTag(closeTagName, elementStack)) {
                    throw new XmlSyntaxError(String.format("Opening and closing tag names are different (opened '%s', but trying to close '%s')", elementStack.peek().getName(), closeTagName));
                }

                root = elementStack.pop();
            } else if (isSelfClose(tag)) {
                // Create element object
                Element element = new Element(
                        getName(noBreaksTag),
                        null,
                        getAttributes(noBreaksTag)
                );

                // Adding element as child for current parent
                // No need to track it in the stack
                addAsChildToParent(element, elementStack);

                // Tag is self-closing, so it is representing null value
                element.setNullValue(true);
            } else {
                // Tag has wrong syntax
                throw new XmlSyntaxError("Xml tag has wrong syntax");
            }
        }

        if (!elementStack.isEmpty()) {
            throw new XmlSyntaxError(String.format("(%d) opened %s were not closed", elementStack.size(), elementStack.size() == 1 ? "tag" : "tags"));
        }

        return new Xml(root);
    }

    private String skipConfigTag(String data) {
        int start = 0;
        Matcher configTagsMatcher = xmlConfigTagPattern.matcher(data);
        while (configTagsMatcher.find()) {
            start = configTagsMatcher.end();
        }
        return data.substring(start);
    }

    private void addAsChildToParent(Element element, Stack<Element> elementStack) {
        // Add element as child and set its parent
        if (!elementStack.isEmpty()) {
            Element parent = elementStack.peek();
            parent.addChild(element);
            element.setParent(parent);
        } // if stack is empty, element is being root and nothing can have it as child
    }

    private boolean isClosingOpenedTag(String name, Stack<Element> elementStack) {
        return !elementStack.isEmpty() && name.equals(elementStack.peek().getName());
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
            throw new XmlSyntaxError();
        }
        String value = valueMatcher.group();
        return value.isEmpty() ? null : value;
    }

    private String getName(String noBreaksTag) {
        Matcher matcher = Pattern.compile("^[a-z0-9_\\-]+").matcher(noBreaksTag);
        if (!matcher.find()) {
            throw new XmlSyntaxError("Tag name is incorrect");
        }
        return matcher.group();
    }

    private String removeTagBreaks(String tag) {
        tag = tag.replaceAll("</?\\s*", "");
        tag = tag.replaceAll("\\s*/?>", "");
        return tag;
    }
}
