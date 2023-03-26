package managers.taskManager.httpManager;

import managers.taskManager.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient client;
    String uri;
    private String api;


    public KVTaskClient(String uri) {
        this.uri = uri;
        client = HttpClient.newHttpClient();
        URI url = URI.create(uri + "/register/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                api = response.body();
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("error while connecting to KVServer");
        }
    }

    public void put(String key, String value) {
        URI url = URI.create(uri + "/save/" + key + "?API_TOKEN=" + api);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Successfully saved");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("error while saving to KVServer");
        }
    }

    public String load(String key) {
        URI url = URI.create(uri + "/load/" + key + "?API_TOKEN=" + api);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
                return null;
            }
        } catch (NullPointerException | IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" + "Проверьте, пожалуйста, адрес и повторите попытку.");
            return null;
        }
    }
}