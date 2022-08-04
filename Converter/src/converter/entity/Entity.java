package converter.entity;

import converter.Element;

public interface Entity {
    Element getRoot();

    enum Type {
        JSON,
        XML,
        YAML
    }
}
