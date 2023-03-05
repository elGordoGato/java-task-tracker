package managers.taskManager;
import managers.Managers;
import managers.historyManager.HistoryManager;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    static final String fileName = "BackedTasks.csv";

    public FileBackedTasksManager() {
        try {
            Path backedTasks = Files.createFile(Paths.get(fileName));

            if (Files.exists(backedTasks)) {
                System.out.println("Файл " + fileName + " успешно создан.");
            }
        } catch (IOException ignored) {
        }
    }

    static FileBackedTasksManager loadFromFile() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        try {
            String singleLineData = Files.readString(Path.of(fileName));
            String[] linesData = singleLineData.split("\n");
            for (int i = 0; i < linesData.length-2; i++) {
                    Task task = fileBackedTasksManager.fromString(linesData[i]);
                    fileBackedTasksManager.createTask(task);
            }
            if (linesData.length!=0) {
                for (Integer hashId : historyFromString(linesData[linesData.length - 1])) {
                    fileBackedTasksManager.getByID(hashId);
                }
            }
        } catch (IOException ignored) {
        }
        return fileBackedTasksManager;
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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            for (int Id : allTasks.keySet()) {
                fileWriter.write(allTasks.get(Id).toString() + "\n");
            }
            for (int Id : allEpics.keySet()) {
                fileWriter.write(allEpics.get(Id).toString() + "\n");
            }
            for (int Id : allSubtasks.keySet()) {
                fileWriter.write(allSubtasks.get(Id).toString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));
        } catch (IOException exc) {
            throw new ManagerSaveException(exc.getMessage());
        }
    }

    private Task fromString(String line) {
        String[] task = line.split(",");
        if (task.length >= 2) {
            switch (task[1]) {
                case "TASK":
                    return new Task(task[0], task[1], task[2], task[3], task[4]);               //id,type,name,status,description,epic
                case "EPIC":
                    return new Epic(task[0], task[1], task[2], task[3], task[4]);
                case "SUBTASK":
                    return new SubTask(task[0], task[1], task[2], task[3], task[4], task[5]);   //3,SUBTASK,Sub Task2,DONE,Description sub task3,2
            }
        }
        return null;
    }

    static String historyToString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            sb.append(task.getID()).append(",");
        }
        if (sb.length() != 0) {
            sb.replace(sb.length() - 1, sb.length(), "");
        } else {
            sb.append("no history yet");
        }
        return sb.toString();
    }

    static List<Integer> historyFromString(String value) throws IOException {
        String[] stringData = value.split(",");
        Integer[] intData = new Integer[stringData.length];
        try {
            for (int i = 0; i < stringData.length; i++) {
                intData[i] = Integer.parseInt(stringData[i]);
            }
        } catch (NumberFormatException exc){
            throw new IOException();
        }
        return Arrays.asList(intData);
    }


    public static void main(String[] args) {
        TaskManager taskManager = new FileBackedTasksManager();

        Task task1 = new Task("1task: pay for water", "too expensive");
        Task task2 = new Task("2task: pay for electricity", "also too expensive");
        Epic epic1 = new Epic("1Epic", "bold");
        SubTask subTask1 = new SubTask(epic1.getID(), "1.1Subtask", "it will bite");
        SubTask subTask2 = new SubTask(epic1.getID(), "1.2Subtask", "only royal canin");
        SubTask subTask3 = new SubTask(epic1.getID(), "1.3Subtask", "Unrequited love");
        Epic epic2 = new Epic("2Epic", "nothing is clear");

            taskManager.createTask(task1);
            taskManager.createTask(task2);
            taskManager.createTask(epic1);
            taskManager.createTask(subTask1);
            taskManager.createTask(subTask2);
            taskManager.createTask(subTask3);
            taskManager.createTask(epic2);

            //Testing
            taskManager.getByID(task1.getID());
            taskManager.getByID(task2.getID());
            taskManager.getByID(subTask3.getID());
            taskManager.getByID(epic1.getID());
            taskManager.getByID((task1.getID()));

        taskManager.printAllTasks();
        System.out.println();
        System.out.println(Managers.getDefaultHistory().getHistory().toString());
        System.out.println();

        System.out.println("new taskManager");
        FileBackedTasksManager fileBackedTasksManager2 = loadFromFile();
        fileBackedTasksManager2.printAllTasks();
        System.out.println(fileBackedTasksManager2.historyManager.getHistory().toString());
    }
}

