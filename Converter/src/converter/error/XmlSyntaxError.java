package converter.error;

public class XmlSyntaxError extends Error {
    public XmlSyntaxError(String message) {
        super(message);
    }

    public XmlSyntaxError() {
        super();
    }
}
