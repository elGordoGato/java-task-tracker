package managers.historyManager;

import managers.taskManager.FileBackedTasksManager;
import managers.taskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Type;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    FileBackedTasksManager historyTestTaskManager;
    Task task1;
    Task task2;
    Task epic1;
    Task sub1;
    Task sub2;
    Task sub3;
    Task epic2;

    @BeforeEach
    void setUp() {
        historyTestTaskManager = new FileBackedTasksManager();
        task1 = new Task("Task1", "T1descr",
                null, null);
        task2 = new Task("Task2", "T2descr",
                "23.12.1984 10:20", "754");
        epic1 = new Epic("Epic1", "E1descr");
        sub1 = new SubTask(epic1.getID(), "SubTask1", "S1descr",
                null, null);
        sub2 = new SubTask(epic1.getID(), "SubTask2", "S2descr",
                "24.12.1984 16:24", "44");
        sub3 = new SubTask("111", "SubTask3", "IN_PROGRESS", "S3descr",
                "24.12.1984 16:25", "50", String.valueOf(epic1.getID()));
        epic2 = new Epic("123", "Epic2", "");
        fillTaskManager();
    }

    private void fillTaskManager() {
        historyTestTaskManager.createTask(task1);
        historyTestTaskManager.createTask(task2);
        historyTestTaskManager.createTask(epic1);
        historyTestTaskManager.createTask(sub1);
        historyTestTaskManager.createTask(sub2);
        historyTestTaskManager.createTask(epic2);
        historyTestTaskManager.getByID(task1.getID());
        historyTestTaskManager.getByID(epic1.getID());
        historyTestTaskManager.getByID(epic2.getID());
        historyTestTaskManager.getByID(sub2.getID());
    }

    @Test
    void shouldOverwriteHistoryOfGetByIdWhenCallingAgain() {
        historyTestTaskManager.getByID(task1.getID());
        assertEquals(List.of(epic1, epic2, sub2, task1), historyTestTaskManager.historyManager.getHistory());
        FileBackedTasksManager loadedTaskManager = FileBackedTasksManager.loadFromFile();
        assertEquals(List.of(epic1, epic2, sub2, task1), loadedTaskManager.historyManager.getHistory());
    }

    @Test
    void shouldDoNothingWhenCallingGetByIDWithNoArgument() {
        historyTestTaskManager.getByID(findUniqueId(historyTestTaskManager));
        assertEquals(List.of(task1, epic1, epic2, sub2), historyTestTaskManager.historyManager.getHistory());
    }

    @Test
    void shouldRemoveTasksFromHistoryWhenRemoveByType() {
        historyTestTaskManager.deleteByType(Type.TASK);
        assertEquals(List.of(epic1, epic2, sub2), historyTestTaskManager.historyManager.getHistory());
        FileBackedTasksManager loadedTaskManager = new FileBackedTasksManager();
        assertEquals(List.of(epic1, epic2, sub2), loadedTaskManager.historyManager.getHistory());

    }

    @Test
    void shouldRemoveSubTasksFromHistoryWhenRemovingEpic() {
        historyTestTaskManager.removeById(epic1.getID());
        assertEquals(List.of(task1, epic2), historyTestTaskManager.historyManager.getHistory());
        FileBackedTasksManager loadedTaskManager = new FileBackedTasksManager();
        assertEquals(List.of(task1, epic2), loadedTaskManager.historyManager.getHistory());
    }

    @Test
    void shouldClearAllHistoryWhenClearAllTasks() {
        historyTestTaskManager.deleteAllTasks();
        assertEquals(List.of(), historyTestTaskManager.historyManager.getHistory());
        FileBackedTasksManager loadedTaskManager = new FileBackedTasksManager();
        assertEquals(List.of(), loadedTaskManager.historyManager.getHistory());
    }

    @Test
    void shouldUpdateSubtasksWhenUpdatingTheirEpic() {
        historyTestTaskManager.getByID(sub1.getID());
        Epic newEpic = new Epic(String.valueOf(epic1.getID()), "Changed Epic", "new descr");
        historyTestTaskManager.updateTask(newEpic);
        assertEquals(List.of(task1, epic1, epic2), historyTestTaskManager.historyManager.getHistory());
    }


    private int findUniqueId(TaskManager testTaskManager) {
        int randomId = 0;
        boolean isIdUnique = false;
        while (!isIdUnique) {
            isIdUnique = true;
            randomId = (int) (Math.random() * 100000 + 1);
            for (Task task : testTaskManager.getAllTasks()) {
                if (task.getID() == randomId) {
                    isIdUnique = false;
                    break;
                }
            }
        }
        return randomId;
    }
}