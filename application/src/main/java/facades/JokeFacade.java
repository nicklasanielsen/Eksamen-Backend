package facades;

import DTOs.JokeDTO;
import com.google.gson.JsonObject;
import errorhandling.exceptions.FetchException;
import handlers.JokeFetchHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Nicklas Nielsen
 */
public class JokeFacade {

    private static JokeFacade instance;

    private JokeFacade() {
        // Private constructor to ensure Singleton
    }

    public static JokeFacade getJokeFacade() {
        if (instance == null) {
            instance = new JokeFacade();
        }

        return instance;
    }

    public List<JokeDTO> getJokes(ExecutorService threadPool) throws FetchException {
        Map<String, String> requests = getRequestsForJokes();

        List<Future<JsonObject>> futures = new ArrayList<>();

        // Starting all treads / callables
        for (Map.Entry<String, String> request : requests.entrySet()) {
            JokeFetchHandler handler = new JokeFetchHandler(request.getKey(), request.getValue());
            Future<JsonObject> futureJson = threadPool.submit(handler);
            futures.add(futureJson);
        }

        List<JokeDTO> jokeDTOs = new ArrayList<>();

        try {
            // Getting results
            // Timeout set to 10 seconds
            for (Future<JsonObject> future : futures) {
                JsonObject jsonObject = future.get(10, TimeUnit.SECONDS);
                jokeDTOs.add(convertToJokeDTO(jsonObject));
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            if (e instanceof TimeoutException) {
                throw new FetchException("One or more of our partners did not respond when we tried to load one or more jokes, please try again later..", 503);
            }

            throw new FetchException("A system error occurred when we first loaded one or more jokes from our partners, please contact us regarding the error, or try again later.", 500);
        }

        return jokeDTOs;
    }

    private Map<String, String> getRequestsForJokes() {
        // <URL, jokeValue>
        Map<String, String> requests = new HashMap<>();

        requests.put("https://api.chucknorris.io/jokes/random", "value");
        requests.put("https://icanhazdadjoke.com", "joke");
        requests.put("https://api.yomomma.info", "joke");
        requests.put("https://api.chucknorris.io/jokes/random/", "value");
        requests.put("https://icanhazdadjoke.com/", "joke");
        requests.put("https://api.yomomma.info/", "joke");

        return requests;
    }

    private JokeDTO convertToJokeDTO(JsonObject jsonObject) throws FetchException {
        String jokeValue, ref, joke;

        try {
            jokeValue = jsonObject.get("jokeValue").getAsString();

            ref = jsonObject.get("jokeRef").getAsString();
            joke = jsonObject.get(jokeValue).getAsString();
        } catch (NullPointerException e) {
            throw new FetchException("A system error occurred while converting one or more jokes from our partners, please contact us regarding the error.", 500);
        }

        return new JokeDTO(ref, joke);
    }

}
