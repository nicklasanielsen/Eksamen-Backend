package handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import errorhandling.exceptions.FetchException;
import java.util.concurrent.Callable;
import utils.HttpUtils;

/**
 *
 * @author Nicklas Nielsen
 */
public class JokeFetchHandler implements Callable<JsonObject> {

    private String URL;
    private String jokeValue;

    public JokeFetchHandler(String URL, String jokeValue) {
        this.URL = URL;
        this.jokeValue = jokeValue;
    }

    @Override
    public JsonObject call() throws FetchException {
        String fetchedData = HttpUtils.fetchData(URL);
        return convertToJSON(fetchedData);
    }

    private JsonObject convertToJSON(String fetchedData) throws FetchException {
        JsonObject jsonObject = JsonParser.parseString(fetchedData).getAsJsonObject();
        jsonObject.addProperty("jokeRef", URL);
        jsonObject.addProperty("jokeValue", jokeValue);

        return jsonObject;
    }

}
