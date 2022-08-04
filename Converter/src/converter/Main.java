package converter;

import converter.entity.Entity;
import converter.entity.Json;
import converter.entity.Xml;
import converter.parser.JsonParser;
import converter.parser.XmlParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String filename = "test.txt";
        String data = readFileData(filename);
        Entity.Type dataType = getDataType(data);
        JsonParser jsonParser = new JsonParser();
        XmlParser xmlParser = new XmlParser();
        // yaml parser

        Entity entity, convertedEntity;
        switch (dataType) {
            case JSON:
                entity = jsonParser.parse(data);
                convertedEntity = Converter.fromJsonToXml((Json) entity);
                break;
            case XML:
                entity = xmlParser.parse(data);
                convertedEntity = Converter.fromXmlToJson((Xml) entity);
                break;
            case YAML:
                // parse yaml
                convertedEntity = null;
                break;
            default:
                convertedEntity = null;
                break;
        }

        System.out.println(convertedEntity);
    }

    private static String readFileData(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder dataRows = new StringBuilder();
            while (reader.ready()) {
                dataRows.append(reader.readLine().trim());
            }
            return dataRows.toString();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            return "";
        } catch (IOException e) {
            System.err.println("Error while reading file!");
            return "";
        }
    }

    private static Entity.Type getDataType(String data) {
        if (data.charAt(0) == '{') {
            return Entity.Type.JSON;
        } else if (data.charAt(1) == '<') {
            return Entity.Type.XML;
        } else {
            return Entity.Type.YAML;
        }
    }
}
