import managers.taskManager.FileBackedTasksManager;
import managers.taskManager.httpManager.HttpTaskManager;
import managers.taskManager.httpManager.KVServer;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskManager httpTaskServer = new HttpTaskManager("http://localhost:8078");
        Epic epic = new Epic("epic", "hello");

        httpTaskServer.createTask(new Task("title", "hello", null, null));
        httpTaskServer.createTask(epic);
        httpTaskServer.createTask(new SubTask(epic.getID(), "title", "hello", "25.12.1864 13:45", "695"));
        httpTaskServer.createTask(new Task("T2", "Descr2", "12.05.1995 15:49", "847"));
        httpTaskServer.getByID(epic.getID());

        FileBackedTasksManager httpTaskManager = new HttpTaskManager("http://localhost:8078", true);
        System.out.println(httpTaskManager.getAllTasks());
        System.out.println(httpTaskManager.historyManager.getHistory());
        System.out.println(httpTaskManager.getPrioritizedTasks());


    }
}
