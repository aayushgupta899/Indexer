package utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * Defines methods to read a JSON file into an object
 */
public class JSONFileReader {

    /**
     * Reads from an JSON file
     * @param filename The name of the JSON file
     * @return A JSON Array containing the data
     */
    public JSONArray readFromJSONFile(String filename)
    {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filename))
        {
            JSONObject json = (JSONObject) jsonParser.parse(reader);
            return (JSONArray) json.get("corpus");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
