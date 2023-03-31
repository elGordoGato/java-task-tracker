package managers.taskManager.httpManager;

import managers.taskManager.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient client;
    private final String uri;
    private final String api;


    public KVTaskClient(String uri) {
        this.uri = uri;
        client = HttpClient.newHttpClient();
        api = register(uri);
    }

    protected void put(String key, String value) {
        URI url = URI.create(uri + "/save/" + key + "?API_TOKEN=" + api);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .build();
        try {
            final HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("error while saving to KVServer");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("error while saving to KVServer");
        }
    }

    protected String load(String key) {
        URI url = URI.create(uri + "/load/" + key + "?API_TOKEN=" + api);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Error while loading: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new ManagerSaveException("Error while loading");
        }
    }

    private String register(String uri) {
        URI url = URI.create(uri + "/register/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("error while connecting to KVServer");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("error while connecting to KVServer");
        }
    }
}