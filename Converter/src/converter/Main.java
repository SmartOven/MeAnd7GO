package converter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String filename = "test.txt";
        String data = readFileData(filename);

    }

    private static String readFileData(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder dataRows = new StringBuilder();
            while (reader.ready()) {
                dataRows.append(reader.readLine());
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
}
