import managers.taskManager.FileBackedTasksManager;
import managers.taskManager.httpManager.HttpTaskServer;
import managers.taskManager.httpManager.KVServer;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();
        FileBackedTasksManager httpTaskServer = new FileBackedTasksManager();
        Epic epic = new Epic("epic", "hello");

        httpTaskServer.createTask(new Task("tiitl", "hello", null, null));
        httpTaskServer.createTask(epic);
        httpTaskServer.createTask(new SubTask(epic.getID(), "tiitl", "hello", "25.12.1864 13:45", "695"));
        httpTaskServer.createTask(new Task("T2", "Descr2", "12.05.1995 15:49", "847"));
        httpTaskServer.getByID(epic.getID());

        FileBackedTasksManager httpTaskManager = FileBackedTasksManager.loadFromFile();
        System.out.println(httpTaskManager.getAllTasks());
        System.out.println(httpTaskManager.historyManager.getHistory());


    }
}
