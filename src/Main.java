import managers.Managers;
import managers.taskManager.TaskManager;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("task: pay for water", "too expensive");
        taskManager.createTask(task1);
        Task task2 = new Task("task2: pay for electricity", "also too expensive");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("zavedi kota", "bold");
        taskManager.createTask(epic1);
        SubTask subTask1 = new SubTask(epic1.getID(), "poglad kota", "it will bite");
        taskManager.createTask(subTask1);
        SubTask subTask2 = new SubTask(epic1.getID(),"nakormi kota", "only royal canin");
        taskManager.createTask(subTask2);
        SubTask subTask3 = new SubTask(epic1.getID(),"lubi kota", "Unrequited love");
        taskManager.createTask(subTask3);

        Epic epic2 = new Epic("Pass TZ", "nothing is clear");
        taskManager.createTask(epic2);

        //Testing
        taskManager.getByID(task1.getID());
        taskManager.getByID(task2.getID());
        taskManager.getByID(subTask3.getID());
        taskManager.getByID(epic1.getID());
        taskManager.getByID((task1.getID()));

        taskManager.removeById(epic1.getID());

        for (Task string : Managers.getDefaultHistory().getHistory()){
            System.out.println(string);
        }


    }


}
