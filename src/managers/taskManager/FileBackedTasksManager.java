package managers.taskManager;

import tasks.Task;
import tasks.Type;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    static final String fileName = "BackedTasks.csv";

    public FileBackedTasksManager() {
        try {
            Path backedTasks = Paths.get(fileName);
            if (!Files.exists(backedTasks)) {
                Files.createFile(backedTasks);
            }
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private static String historyToString() {
        StringBuilder sb = new StringBuilder();
        for (Task task : TaskManager.historyManager.getHistory()) {
            sb.append(task.getID()).append(",");
        }
        if (sb.length() != 0) {
            sb.replace(sb.length() - 1, sb.length(), "");
        } else {
            sb.append("no history yet");
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) throws IOException {
        String[] stringData = value.split(",");
        Integer[] intData = new Integer[stringData.length];
        if (!value.contains("no history yet")) {
            try {
                for (int i = 0; i < stringData.length; i++) {
                    intData[i] = Integer.parseInt(stringData[i]);
                }
            } catch (NumberFormatException exc) {
                System.out.println(exc.getMessage());
            }
        }
        return Arrays.asList(intData);
    }

    public static FileBackedTasksManager loadFromFile() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        try {
            String singleLineData = Files.readString(Path.of(fileName));
            if (!singleLineData.isEmpty()) {
                String[] linesData = singleLineData.split("\n");

                if (linesData.length != 0 && !linesData[0].isEmpty()) {
                    for (int i = 0; i < linesData.length - 2; i++) {
                        Task task = fileBackedTasksManager.fromString(linesData[i]);
                        fileBackedTasksManager.createTask(task);
                    }
                    for (Integer hashId : historyFromString(linesData[linesData.length - 1])) {
                        if (hashId != null) {
                            fileBackedTasksManager.addToHistory(hashId);
                        }
                    }
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        return fileBackedTasksManager;
    }

    protected void addToHistory(Integer id) {
        if (allTasks.containsKey(id)) {
            historyManager.add(allTasks.get(id));
        }
        if (allEpics.containsKey(id)) {
            historyManager.add(allEpics.get(id));
        }
        if (allSubtasks.containsKey(id)) {
            historyManager.add(allSubtasks.get(id));
        }
    }

    @Override
    public Task getByID(int hashId) {
        Task task = super.getByID(hashId);
        save();
        return task;
    }

    @Override
    public void createTask(Task newTask) {
        super.createTask(newTask);
        save();
    }

    @Override
    public void removeById(int hashId) {
        super.removeById(hashId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteByType(Type type) {
        super.deleteByType(type);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    protected void save() {
        try (BufferedWriter fileWriter = Files.newBufferedWriter(Path.of(fileName), StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int Id : allTasks.keySet()) {
                fileWriter.write(allTasks.get(Id).toString() + "\n");
            }
            for (int Id : allEpics.keySet()) {
                fileWriter.write(allEpics.get(Id).toString() + "\n");
                for (SubTask sub : allEpics.get(Id).getTaskList()) {
                    fileWriter.write(sub.toString() + "\n");
                }
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString());
        } catch (IOException exc) {
            throw new ManagerSaveException(exc.getMessage());
        }
    }

    private Task fromString(String line) {
        String[] task = line.split(",");
        if (task.length >= 2) {
            switch (task[1]) {
                case "TASK":
                    return new Task(task[0], task[2], task[3], task[4], task[5], task[6]);               //id,type,name,status,description,epic
                case "EPIC":
                    return new Epic(task[0], task[2], task[4]);
                case "SUBTASK":
                    return new SubTask(task[0], task[2], task[3], task[4], task[5], task[6], task[7]);   //3,SUBTASK,Sub Task2,DONE,Description sub task3,2
            }
        }
        return null;
    }
}

