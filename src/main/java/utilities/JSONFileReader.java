package utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JSONFileReader {

    public JSONArray readFromJSONFile(String filename)
    {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filename))
        {
            //Read JSON file
            JSONObject json = (JSONObject) jsonParser.parse(reader);
            return (JSONArray) json.get("corpus");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
