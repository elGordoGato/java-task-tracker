import taskManager.TaskManager;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("task: pay for water", "too expensive");
        taskManager.createTask(task1);
        Task task2 = new Task("task2: pay for electricity", "too expensive");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("zavedi kota", "bold");
        taskManager.createTask(epic1);

        SubTask subTask1 = new SubTask(epic1.getID(), "poglad kota", "it will bite");
        taskManager.createTask(subTask1);
        SubTask subTask2 = new SubTask(epic1.getID(),"nakormi kota", "only royal canin");
        taskManager.createTask(subTask2);

        Epic epic2 = new Epic("Zhivi", "Na chile");
        taskManager.createTask(epic2);
        SubTask subTask3 = new SubTask(epic2.getID(), "Lech na divan", "cozy");
        taskManager.createTask(subTask3);

        System.out.println(taskManager.allTasks);
        taskManager.printAllTasks();
        System.out.println();
        subTask3.updateStatus();
        task2.updateStatus();
        taskManager.updateTask(task2);
        taskManager.updateTask(subTask3);
        taskManager.printAllTasks();

        Task task11 = new Task("!!!task: pay for water", "too expensive");
        taskManager.createTask(task11);
        task11.setId(11);
        taskManager.updateTask(task11);
        taskManager.printAllTasks();
        Task task11FromRepo = taskManager.getByID(11);
        System.out.println();
        System.out.println(task11FromRepo);

    }


}
