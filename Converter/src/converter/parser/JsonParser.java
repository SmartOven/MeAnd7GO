package converter.parser;

import converter.Element;
import converter.entity.Json;
import converter.error.JsonSyntaxError;

import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParser implements Parser<Json> {

//    private static final Pattern keyValueWithBreaksPattern;
//    private static final Pattern keyValueWithoutBreaksPattern;
//    private static final Pattern keyValueObjectPattern;
//    private static final Pattern keyValueArrayPattern;


//    private static final Pattern stringValuePattern;
//    private static final Pattern otherValuePattern;
    private static final Pattern keyPattern;
    private static final Pattern valuePattern;

    private static final Pattern objectStartPattern;
    private static final Pattern objectEndPattern;
    private static final Pattern arrayStartPattern;
    private static final Pattern arrayEndPattern;

    private static final Pattern separatorPattern;

    private String data;
    private int searchingIndex;

    static {
//        keyValueWithBreaksPattern = Pattern.compile("\\s*\"([^\"]+?)\"\\s*:\\s*\"(.*)\"");
//        keyValueWithoutBreaksPattern = Pattern.compile("\\s*\"([^\"]+?)\"\\s*:\\s*([\\w\\d\\-.]+)");
//        keyValueObjectPattern = Pattern.compile("\\s*\"([^\"]+?)\"\\s*:\\s*\\{");
//        keyValueArrayPattern = Pattern.compile("\\s*\"([^\"]+?)\"\\s*:\\s*\\[");

        keyPattern = Pattern.compile("\\s*\"([^\"]+?)\"\\s*:\\s*");
        valuePattern = Pattern.compile("\\s*(\"([^\"]*)\")|(null|true|false|[0-9]*\\.?[0-9]+)\\s*");
//        stringValuePattern = Pattern.compile("\\s*\"([^\"]*)\"\\s*");
//        otherValuePattern = Pattern.compile("\\s*(null|true|false|[0-9]*\\.?[0-9]+)\\s*");

        objectStartPattern = Pattern.compile("\\s*\\{\\s*");
        objectEndPattern = Pattern.compile("\\s*}\\s*");
        arrayStartPattern = Pattern.compile("\\s*\\[\\s*");
        arrayEndPattern = Pattern.compile("\\s*]\\s*");

        separatorPattern = Pattern.compile("\\s*,\\s*");
    }

    /**
     * <h3>Parsing Json file data trough key-value pairs:</h3>
     * <p>Root JSON object is unnamed, so the name is "root" by default</p>
     * <p>While parsing we are either inside an object or inside an array</p>
     * <ol>
     *     <li>
     *         <p><b>For object</b> we have key-value pairs</p>
     *         <ol>
     *             <li><b>@</b> means parents attribute</li>
     *             <li><b>#</b> means parents value</li>
     *             <li>others are children</li>
     *         </ol>
     *     </li>
     *     <li><b>For array</b> we only have values (every key is named "element", every element is child)</li>
     * </ol>
     *
     * @param text String with Json file data
     * @return Json object
     */
    @Override
    public Json parse(String text) {
        data = text;
        searchingIndex = 0;

        Element root = new Element("root", null, new ArrayList<>());
        Stack<Element> elementStack = new Stack<>();
        elementStack.add(root);

        data = data.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
        if (!data.startsWith("{") || !data.endsWith("}")) {
            throw new JsonSyntaxError("Not a JSON object");
        }
        data = data.substring(1, data.length() - 1);

        boolean insideObject = true; // if false, we are inside an array

//        var isComplex = ()
//        Consumer<Boolean>

        while (searchingIndex < data.length()) {
            if (elementStack.isEmpty()) {
                throw new JsonSyntaxError("Not a JSON object");
            }

            // If we are inside object -> key, then value (with breaks, without breaks, object or array)
            // otherwise (we are inside array) -> just value (with breaks, without breaks, object or array)
            String key = insideObject ? findKey() : "element";

            if (isValueObject()) {
                elementStack.add(new Element(key, null, null));
                insideObject = true;
                continue;
            }
            if (isValueArray()) {
                elementStack.add(new Element(key, null, null));
                insideObject = false;
                continue;
            }

            Element parent = elementStack.peek();

            String value = findValue();
            if (isAttribute(key)) {
                parent.addAttribute(new Element.Attribute(key, value));
                continue;
            }
            if (isValue(key)) {
                parent.setValue(value);
                continue;
            }
            parent.addChild(new Element(key, value, null));
        }
        return null;
    }

    private boolean found(Pattern pattern) {
        Matcher matcher = pattern.matcher(data);
        boolean isFound = matcher.find(searchingIndex);
        int startIndex = -1;
        if (isFound) {
            startIndex = matcher.start();
        }
        if (isFound && startIndex == searchingIndex) {
            searchingIndex = matcher.end();
            return true;
        }
        return false;
    }

    private Matcher findPattern(Pattern pattern) {
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find(searchingIndex)) {
            throw new JsonSyntaxError("Not a JSON object");
        }
        searchingIndex = matcher.end();
        return matcher;
    }

    private String findKey() {
        return findPattern(keyPattern).group();
    }

    private String findValue() {
        Matcher matcher = findPattern(valuePattern);

        // If value is null - return null, otherwise return value
        String stringValue = matcher.group(0);
        String otherValue = matcher.group(1);
        if (stringValue != null) {
            return stringValue;
        }
        if (!"null".equals(otherValue)) {
            return otherValue;
        }
        return null;
    }

    private boolean isValueComplex() {
        return isValueObject() || isValueArray();
    }

    private boolean isValueObject() {
        return false;
    }

    private boolean isValueArray() {
        return false;
    }

    private boolean isAttribute(String key) {
        return key.charAt(0) == '@';
    }

    private boolean isValue(String key) {
        return key.charAt(0) == '#';
    }

//    private String getName(String nameString) {
//        return nameString.replaceAll("^\\s*\"", "").replaceAll("\"\\s*:\\s*$", "");
//    }
//
//    private String getValue(String valueString) {
//        return valueString.replaceAll("^\\s*\"?", "").replaceAll("\"?\\s*[,}]$", "");
//    }

    public static void main(String[] args) {
        JsonParser parser = new JsonParser();
        parser.parse(text);
    }

    private static final String text = """
            {
                "transaction": {
                    "id": "6753322",
                    "number": {
                        "@region": "Russia",
                        "#number": "8-900-000-000"
                    },
                    "special1": false,
                    "special2": true,
                    "empty1": null,
                    "empty2": { },
                    "empty3": [ ],
                    "empty4": {},
                    "empty5": [],
                    "empty6": {

                    },
                    "empty7": [

                    ],
                    "empty8": "",
                    "array1": [
                        null, null
                    ],
                    "array2": [
                        [],
                        true, false, null,
                        123, 123.456,
                        "",
                        {
                            "key1": "value1",
                            "key2": {
                                "@attr": "value2",
                                "#key2": "value3"
                            }
                        },
                        {
                            "@attr2": "value4",
                            "#element": "value5"
                        }
                        ,
                        {
                            "@attr3": "value4",
                            "#elem": "value5"
                        },
                        {
                            "#element": null
                        },
                        {
                            "#element": {
                                "deep": {
                                    "@deepattr": "deepvalue",
                                    "#deep": [
                                        1, 2, 3
                                    ]
                                }
                            }
                        }
                    ],
                    "inner1": {
                        "inner2": {
                            "inner3": {
                                "key1": "value1",
                                "key2": "value2"
                            }
                        }
                    },
                    "inner4": {
                        "@": 123,
                        "#inner4": "value3"
                    },
                    "inner5": {
                        "@attr1": 123.456,
                        "#inner4": "value4"
                    },
                    "inner6": {
                        "@attr2": 789.321,
                        "#inner6": "value5"
                    },
                    "inner7": {
                        "#inner7": "value6"
                    },
                    "inner8": {
                        "@attr3": "value7"
                    },
                    "inner9": {
                        "@attr4": "value8",
                        "#inner9": "value9",
                        "something": "value10"
                    },
                    "inner10": {
                        "@attr5": null,
                        "#inner10": null
                    },
                    "inner11": {
                        "@attr11": "value11",
                        "#inner11": {
                            "inner12": {
                                "@attr12": "value12",
                                "#inner12": {
                                    "inner13": {
                                        "@attr13": "value13",
                                        "#inner13": {
                                            "inner14": "v14"
                                        }
                                    }
                                }
                            }
                        }
                    },
                    "inner15": {
                        "@": null,
                        "#": null
                    },
                    "inner16": {
                        "@somekey": "attrvalue",
                        "#inner16": null,
                        "somekey": "keyvalue",
                        "inner16": "notnull"
                    },
                    "crazyattr1": {
                        "@attr1": 123,
                        "#crazyattr1": "v15"
                    },
                    "crazyattr2": {
                        "@attr1": 123.456,
                        "#crazyattr2": "v16"
                    },
                    "crazyattr3": {
                        "@attr1": null,
                        "#crazyattr3": "v17"
                    },
                    "crazyattr4": {
                        "@attr1": true,
                        "#crazyattr4": "v18"
                    },
                    "crazyattr5": {
                        "@attr1": false,
                        "#crazyattr5": "v19"
                    },
                    "crazyattr6": {
                        "@attr1": "",
                        "#crazyattr6": "v20"
                    },
                    "crazyattr7": {
                        "@attr1": {},
                        "#crazyattr7": "v21"
                    },
                    "crazyattr9": {
                        "@attr1": {
                            "@": 1,
                            "#": 2,
                            "": 3,
                            "key": 4
                        },
                        "#crazyattr9": "v23"
                    },
                    "crazyattr10": {
                        "@attr1": [],
                        "#crazyattr10": "v24"
                    },
                    "crazyattr11": {
                        "attr1": "better",
                        "@attr1": {
                            "key9": "value9"
                        },
                        "#crazyattr11": "v25"
                    },
                    "crazyattr12": {
                        "@attr1": [
                            ""
                        ],
                        "#crazyattr12": "v26"
                    },
                    "": {
                        "#": null,
                        "secret": "won't be converted"
                    },
                    "@": 123,
                    "#": [
                        1, 2, 3
                    ]
                },
                "meta": {
                    "version": 0.01
                }
            }""";


}
