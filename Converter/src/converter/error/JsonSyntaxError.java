package converter.error;

public class JsonSyntaxError extends Error {
    public JsonSyntaxError(String message) {
        super(message);
    }

    public JsonSyntaxError() {
        super();
    }
}
