package converter.parser;

import converter.entity.Entity;

public interface Parser<E extends Entity> {
    E parse(String data);
}
