package utils;

import errorhandling.exceptions.FetchException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author Nicklas Nielsen
 */
public class HttpUtils {

    public static String fetchData(String _URL) throws FetchException {
        URL URL;
        HttpURLConnection connection;

        try {
            URL = new URL(_URL);
            connection = (HttpURLConnection) URL.openConnection();

            // Setting Headers
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "server");
        } catch (IOException e) {
            if (e instanceof MalformedURLException) {
                throw new FetchException("An error occurred while trying to load one or more jokes from our partners, please let us know about this error.", 500);
            }

            throw new FetchException("An error occurred while trying to load one or more jokes from our partners, please try again later.", 503);
        }

        Scanner scan = null;
        String jsonStr = null;

        try {
            scan = new Scanner(connection.getInputStream(), "UTF-8");

            if (scan.hasNext()) {
                jsonStr = scan.nextLine();
            }
        } catch (IOException e) {
            throw new FetchException("An error occurred while trying to load one or more jokes from our partners, please let us know about this error.", 500);
        } finally {
            if (scan != null) {
                scan.close();
            }
        }

        return jsonStr;
    }

}
