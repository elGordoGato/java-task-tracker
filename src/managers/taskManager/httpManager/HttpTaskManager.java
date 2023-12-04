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
    private final static String TASK = "tasks";
    private final static String EPIC = "epics";
    private final static String SUBTASK = "subtasks";
    private final static String HISTORY = "history";
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
            HashMap<Integer, Task> tasks = gson.fromJson(kvTaskClient.load(TASK),
                    new TypeToken<HashMap<Integer, Task>>() {
                    }.getType());
            allTasks.putAll(tasks);
            prioritizedTasks.addAll(new ArrayList<>(tasks.values()));
            HashMap<Integer, Epic> epics = gson.fromJson(kvTaskClient.load(EPIC),
                    new TypeToken<HashMap<Integer, Epic>>() {
                    }.getType());
            allEpics.putAll(epics);
            HashMap<Integer, SubTask> subTasks = gson.fromJson(kvTaskClient.load(SUBTASK),
                    new TypeToken<HashMap<Integer, SubTask>>() {
                    }.getType());
            allSubtasks.putAll(subTasks);
            prioritizedTasks.addAll(new ArrayList<>(subTasks.values()));
            ArrayList<Integer> history = gson.fromJson(kvTaskClient.load(HISTORY),
                    new TypeToken<ArrayList<Integer>>() {
                    }.getType());
            history.forEach(this::addToHistory);

        } catch (JsonSyntaxException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Override
    protected void save() {
        kvTaskClient.put(TASK, gson.toJson(allTasks));
        kvTaskClient.put(EPIC, gson.toJson(allEpics));
        kvTaskClient.put(SUBTASK, gson.toJson(allSubtasks));

        String history = gson.toJson(historyManager.getHistory()
                .stream()
                .map(Task::getID)
                .collect(Collectors.toList()));
        kvTaskClient.put(HISTORY, history);
    }
}
