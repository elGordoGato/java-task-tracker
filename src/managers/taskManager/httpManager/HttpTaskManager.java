package managers.taskManager.httpManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import managers.taskManager.FileBackedTasksManager;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HttpTaskManager(String uri, boolean isLoad) {
        kvTaskClient = new KVTaskClient(uri);
        if (isLoad) {
            loadFromServer();
        }
    }

    public HttpTaskManager(String uri) {
        this(uri, false);
    }


    private void loadFromServer() {
        try {
            HashMap<Integer, Task> tasks = gson.fromJson(kvTaskClient.load("tasks"),
                    new TypeToken<HashMap<Integer, Task>>() {
                    }.getType());
            allTasks.putAll(tasks);
            HashMap<Integer, Epic> epics = gson.fromJson(kvTaskClient.load("epics"),
                    new TypeToken<HashMap<Integer, Epic>>() {
                    }.getType());
            allEpics.putAll(epics);
            HashMap<Integer, SubTask> subTasks = gson.fromJson(kvTaskClient.load("subtasks"),
                    new TypeToken<HashMap<Integer, SubTask>>() {
                    }.getType());
            allSubtasks.putAll(subTasks);
            ArrayList<Integer> history = gson.fromJson(kvTaskClient.load("history"),
                    new TypeToken<ArrayList<Integer>>() {
                    }.getType());
            history.forEach(this::getByID);

        } catch (JsonSyntaxException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Override
    protected void save() {
        kvTaskClient.put("tasks", gson.toJson(allTasks));
        kvTaskClient.put("epics", gson.toJson(allEpics));
        kvTaskClient.put("subtasks", gson.toJson(allSubtasks));

        String history = gson.toJson(historyManager.getHistory().stream().map(Task::getID).collect(Collectors.toList()));
        kvTaskClient.put("history", history);
    }
}
