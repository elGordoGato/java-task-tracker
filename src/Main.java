import managers.taskManager.httpManager.HttpTaskManager;
import managers.taskManager.httpManager.KVServer;
import tasks.Task;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final String uri = "http://localhost:8078";

        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskManager httpTaskServer = new HttpTaskManager(uri);

        httpTaskServer.createTask(new Task("tiitl", "hello", "12.04.1994 13:34", "1234"));
        httpTaskServer.createTask(new Task("T2", "Descr2", "12.05.1995 15:49", "847"));
        HttpTaskManager httpTaskManager = new HttpTaskManager(uri);
        System.out.println(httpTaskManager.getAllTasks());


    }
}
