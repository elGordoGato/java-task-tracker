package managers.taskManager.httpManager;

import com.google.gson.JsonSyntaxException;
import managers.taskManager.FileBackedTasksManager;
import tasks.Task;
import tasks.epics.subTasks.SubTask;

import java.io.IOException;

import java.util.Random;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String key = String.valueOf(new Random().nextInt(1000));
    KVTaskClient kvTaskClient;

    public HttpTaskManager(String uri) {
        kvTaskClient = new KVTaskClient(uri);
        //key = String.valueOf(Math.random()*63);
        this.loadFromFile(this);
    }

    @Override
    protected void loadFromFile(FileBackedTasksManager httpTaskManager) {
        try {
            String singleLineData = kvTaskClient.load(key);
            if (!singleLineData.isEmpty()) {
                String[] linesData = singleLineData.split("\n");
                if (!linesData[0].isEmpty()) {
                    for (int i = 0; i < linesData.length - 2; i++) {
                        Task task = httpTaskManager.fromString(linesData[i]);
                        httpTaskManager.createTask(task);
                    }
                    for (Integer hashId : historyFromString(linesData[linesData.length - 1])) {
                        if (hashId != null) {
                            httpTaskManager.getByID(hashId);
                        }
                    }
                }

            }
        } catch (NullPointerException | JsonSyntaxException | IOException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Override
    protected void save() {
        StringBuilder sb = new StringBuilder();
        for (int Id : allTasks.keySet()) {
            sb.append(allTasks.get(Id).toString()).append("\n");
        }
        for (int Id : allEpics.keySet()) {
            sb.append(allEpics.get(Id).toString()).append("\n");
            for (SubTask sub : allEpics.get(Id).getTaskList()) {
                sb.append(sub.toString()).append("\n");
            }
        }
        sb.append("\n");
        sb.append(historyToString());
        kvTaskClient.put(key, sb.toString());
    }

}
