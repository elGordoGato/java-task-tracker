package managers.taskManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.taskManager.httpManager.HttpTaskServer;
import org.junit.jupiter.api.*;
import tasks.Status;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HttpTaskServerTest {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();


    private static Gson gson;

    Task task1 = new Task("Task1", "T1descr",
            null, null);
    Task task2 = new Task("Task2", "T2descr",
            "24.12.1984 16:20", "754");
    Task epic1 = new Epic("Epic1", "E1descr");
    Task sub1 = new SubTask(epic1.getID(), "SubTask1", "S1descr",
            null, null);
    Task sub2 = new SubTask(epic1.getID(), "SubTask2", "S2descr",
            "24.12.1984 16:24", "44");
    Task sub3 = new SubTask("111", "SubTask3", "IN_PROGRESS", "S3descr",
            "25.12.1984 16:25", "50", String.valueOf(epic1.getID()));
    Task epic2 = new Epic("123", "Epic2", "");


    InMemoryTaskManager dataTaskManager = new InMemoryTaskManager();
    HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    static void start() {
        HttpTaskServer.start();
    }

    @AfterAll
    static void stop() {
         HttpTaskServer.stop();
    }

    @BeforeEach
    void setUp() {
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
        dataTaskManager.createTask(task1);
        dataTaskManager.createTask(epic1);
        dataTaskManager.createTask(epic2);
        dataTaskManager.createTask(sub1);
        dataTaskManager.createTask(sub2);
        HttpTaskServer.setTaskManager(dataTaskManager);

    }

    @AfterEach
    void restore() {
    }

    @Test
    void getTasks() throws InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task?/");
        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(gson.toJson(dataTaskManager.getAllTasks()), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void getPriority() throws InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks?/");
        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(gson.toJson(dataTaskManager.getPrioritizedTasks()), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void postTaskShouldAddTask() throws InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/task?/");
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub3))).build();
        URI url = URI.create("http://localhost:8080/tasks/task?id=" + sub3.getID());
        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePost.statusCode());
            HttpResponse<String> response = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
            assertEquals(gson.toJson(sub3), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void postTaskShouldUpdateTask() throws InterruptedException {
        epic2.setDescription("New description");
        URI urlPost = URI.create("http://localhost:8080/tasks/task?/");
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2))).build();
        URI url = URI.create("http://localhost:8080/tasks/task?id=" + epic2.getID());
        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePost.statusCode());
            HttpResponse<String> response = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
            assertEquals(gson.toJson(epic2), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void shouldDeleteAllTasks() throws InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/task?/");
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).DELETE().build();
        URI url = URI.create("http://localhost:8080/tasks/task?/");
        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(205, responsePost.statusCode());
            HttpResponse<String> response = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
            assertEquals("[]", response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void shouldDeleteTaskById() throws InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/task?id=123");
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).DELETE().build();
        URI url = URI.create("http://localhost:8080/tasks/task?/");
        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, responsePost.statusCode());
            HttpResponse<String> response = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
            dataTaskManager.removeById(123);
            assertEquals(gson.toJson(dataTaskManager.getAllTasks()), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void shouldGetEpicBySubtaskId() throws InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/subtask?id=" + sub1.getID());
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).GET().build();
        try {
            HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(String.valueOf(epic1.getID()), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void shouldUpdateSubtaskStatusById() throws InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/epic?id=" + epic1.getID());
        sub2.updateStatus(Status.DONE);
        String newStats = ("{\"id\"=\"" + sub1.getID() + "\",\"status\"=\"DONE\"}");
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).POST(HttpRequest.BodyPublishers
                .ofString(newStats)).build();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=" + epic1.getID());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePost.statusCode());
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(gson.toJson(((Epic) epic1).getTaskList()), response.body());

        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void shouldGetSubTaskListById() throws InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/epic?id=" + epic1.getID());
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).GET().build();
        try {
            HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(gson.toJson(((Epic) epic1).getTaskList()), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void shouldGetEndTimeById() throws InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/epic/endtime?id=" + epic1.getID());
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).GET().build();
        try {
            HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(gson.toJson(epic1.getEndTime()), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void shouldGetHistory() throws InterruptedException {
        dataTaskManager.getByID(123);
        dataTaskManager.getByID(task1.getID());
        HttpTaskServer.setTaskManager(dataTaskManager);
        URI urlPost = URI.create("http://localhost:8080/tasks/history?/");
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).GET().build();
        try {
            HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(gson.toJson(dataTaskManager.historyManager.getHistory()), response.body());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }

    @Test
    void shouldReturn404() throws InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/future?/");
        HttpRequest requestPost = HttpRequest.newBuilder().uri(urlPost).GET().build();
        try {
            HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } catch (IOException exc) {
            throw new ManagerSaveException("IO error while sending occurred");
        }
    }


}




