package managers.taskManager;

import managers.taskManager.httpManager.HttpTaskManager;
import managers.taskManager.httpManager.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    KVServer kvServer;
    String uri = "http://localhost:8078";
    HttpTaskManager httpTaskManagerLoaded;
    HttpTaskManager httpTaskManagerActual;


    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManagerActual = new HttpTaskManager(uri);
        httpTaskManagerActual.createTask(task1);
        httpTaskManagerActual.createTask(epic1);
        httpTaskManagerActual.createTask(sub1);
        httpTaskManagerActual.createTask(sub2);
        httpTaskManagerActual.createTask(task2);
        httpTaskManagerActual.createTask(epic2);
        httpTaskManagerActual.getByID(epic1.getID());
        httpTaskManagerActual.getByID(task1.getID());
        httpTaskManagerActual.getByID(sub2.getID());
        httpTaskManagerLoaded = new HttpTaskManager(uri, true);
        testTaskManager = new HttpTaskManager(uri);
    }


    @AfterEach
    void stop() {
        kvServer.stop();
    }


    @Test
    void shouldReturnSameAllTasks() {
        List<Task> sortedExpected = httpTaskManagerActual.getAllTasks();
        List<Task> sortedActual = httpTaskManagerLoaded.getAllTasks();
        Collections.sort(sortedExpected);
        Collections.sort(sortedActual);
        assertEquals(sortedExpected, sortedActual,
                "Список задач после выгрузки не совпададает");
    }

    @Test
    void shouldReturnSamePrioritizedTasks() {
        assertEquals(httpTaskManagerActual.getPrioritizedTasks(), httpTaskManagerLoaded.getPrioritizedTasks(),
                "Список задач по приоритету после выгрузки не совпададает");
    }

    @Test
    void shouldReturnSameHistory() {
        assertEquals(httpTaskManagerActual.historyManager.getHistory(), httpTaskManagerLoaded.historyManager.getHistory(),
                "История задач после выгрузки не совпададает");
    }
}
