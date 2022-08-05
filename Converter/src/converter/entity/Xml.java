package converter.entity;

import converter.Element;

public class Xml implements Entity {
    private final Element root;

    public Xml(Element root) {
        this.root = root;
    }

    @Override
    public Element getRoot() {
        return root;
    }

    @Override
    public String toString() {
        StringBuilder XmlStringBuilder = new StringBuilder();
        recursivelyBuildXmlString(root, XmlStringBuilder, 0);
        return XmlStringBuilder.toString();
    }

    /**
     * <h3>Recursively building Xml string</h3>
     * <ul>
     *     <li>Step into the recursion while element has children</li>
     *     <li>
     *         <p>Checking tag type:</p>
     *         <ul>
     *             <li><b>If element is self-closing or contain value</b>: add it to the builder</li>
     *             <li><b>If element is not self-closing</b>: add it to the </li>
     *             </ul>
     *     </li>
     * </ul>
     *
     * @param element  current element
     * @param sb       Xml string builder
     * @param depth tag nesting depth
     */
    private void recursivelyBuildXmlString(Element element, StringBuilder sb, int depth) {
        // Adding open tag part (if tag is self-closing, adds full tag)
        sb.append("\t".repeat(depth)).append(elementToXmlTagString(element));

        if (element.isNullElement()) {
            sb.append("\n");
            return; // if element is null, nothing else need to do
        }

        if (element.isArrayElement() && !element.hasChildren()) {
            sb.append("</").append(element.getName()).append(">");
            sb.append("\n");
            return; // if element is representing empty array, just close the tag
        }

        if (element.hasChildren()) {
            sb.append("\n");
            for (Element elementChild : element.getChildren()) {
                recursivelyBuildXmlString(elementChild, sb, depth + 1);
            }
            sb.append("\t".repeat(depth)).append("</").append(element.getName()).append(">");
            sb.append("\n");
            return; // if element has children, make recursion step
        }

        // If element is regular (has no children and has not-null value)
        sb.append(element.getValue());
        sb.append("</").append(element.getName()).append(">");
        sb.append("\n");
    }

    private String elementToXmlTagString(Element element) {
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(element.getName());

        // Add attributes
        for (Element.Attribute attribute : element.getAttributes()) {
            sb.append(' ').append(attribute);
        }

        // If element is null-element, tag is self-closing
        if (element.isNullElement()) {
            sb.append('/');
        }
        sb.append('>');
        return sb.toString();
    }
}
