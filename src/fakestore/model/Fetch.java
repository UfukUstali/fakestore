package fakestore.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.*;
import java.util.*;

class Fetch {
    /**
     * cache to dedupe requests
     * key: path
     * value: response
     */
    private final Map<String, Response> cache = new HashMap<>();
    private final String baseUrl;

    public Fetch(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public IResponse fetch(String path) {
        if (cache.containsKey(path)) return cache.get(path);
        Response response = new Response(Status.PENDING, null, null);
        Thread thread = new Thread(() -> {
            try {
                int MAX_RETRIES = 3;
                int retries = 0;
                // Define the URL
                URL url = new URI(baseUrl + path).toURL();

                System.out.println("query: " + url.getQuery());
                System.out.println("path: " + url.getPath());

                do {
                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    try {
                        // Set the request method to GET
                        connection.setRequestMethod("GET");

                        // Get the response code
                        int responseCode = connection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            // Read the response from the input stream
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder responseString = new StringBuilder();
                            String line;

                            while ((line = reader.readLine()) != null) {
                                responseString.append(line);
                            }

                            reader.close();

                            response.set(Status.SUCCESS, responseString.toString(), null);
                            break;
                        } else {
                            retries++;
                            if (MAX_RETRIES == retries) {
                                response.set(Status.ERROR, null, "Failed to fetch data. Response Code: " + responseCode);
                            }
                        }
                    } catch (Exception e) {
                        retries++;
                        if (MAX_RETRIES == retries) {
                            response.set(Status.ERROR, null, e.getMessage());
                        }
                    } finally {
                        connection.disconnect();
                    }
                } while(MAX_RETRIES > retries);

            } catch (Exception e) {
                e.printStackTrace();
                response.set(Status.ERROR, null, e.getMessage());
            }
        });
        thread.start();
        cache.put(path, response);
        return response;
    }

    public IResponse fetch(String path, Boolean force) {
        if (force) {
            cache.remove(path);
        }
        return fetch(path);
    }
}
