package managers.taskManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Type;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest {

    @BeforeEach
    void setUp() {
        testTaskManager = new FileBackedTasksManager();
    }

    @AfterEach
    void restore() {
        //testTaskManager.deleteAllTasks();
    }

    @Test
    void testCreateTask() {
        testTaskManager.createTask(epic1);
        testTaskManager.createTask(sub2);
        testTaskManager.getByID(sub2.getID());
        FileBackedTasksManager loadedTaskManager = FileBackedTasksManager.loadFromFile();
        assertEquals(List.of(epic1, sub2), loadedTaskManager.getAllTasks());
        assertEquals(List.of(sub2), loadedTaskManager.historyManager.getHistory());
    }

    @Test
    void testRemoveById() {
        testTaskManager.createTask(epic1);
        testTaskManager.createTask(task1);
        testTaskManager.createTask(sub2);
        testTaskManager.removeById(task1.getID());
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile();
        assertEquals(List.of(epic1, sub2), fileBackedTasksManager.getAllTasks());
    }

    @Test
    void testDeleteAllTasks() {
        testTaskManager.createTask(task2);
        testTaskManager.createTask(epic1);
        testTaskManager.createTask(sub1);
        testTaskManager.deleteAllTasks();
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile();
        assertEquals(List.of(), fileBackedTasksManager.getAllTasks());
    }

    @Test
    void testDeleteByType() {
        dataTaskManager.deleteByType(Type.EPIC);
        testTaskManager.createTask(epic1);
        testTaskManager.createTask(task2);
        testTaskManager.createTask(sub1);
        testTaskManager.createTask(task1);
        testTaskManager.deleteByType(Type.TASK);
        testTaskManager.getAllTasks();
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile();
        assertEquals(List.of(epic1, sub1), fileBackedTasksManager.getAllTasks());

    }

    @Test
    void testUpdateTask() {
        Task task10 = new Task("1task: pay for water", "too expensive", null, null);
        Epic epic10 = new Epic("1Epic", "bold");
        SubTask subTask40 = new SubTask(epic10.getID(), "1.4Subtask", "it will bite", null, null);
        SubTask subTask10 = new SubTask(epic10.getID(), "1.1Subtask", "it will bite", "12.12.1994 12:33", "34");
        testTaskManager.createTask(epic10);
        testTaskManager.createTask(subTask10);
        testTaskManager.createTask(task10);
        Epic newEpic = new Epic(epic10);
        newEpic.removeSubtask(subTask10);
        newEpic.addSubTask(subTask40);
        newEpic.setTitle("LAST TEST!!1!11!");
        testTaskManager.updateTask(newEpic);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile();
        assertEquals(List.of(task10, newEpic, subTask40), fileBackedTasksManager.getAllTasks());

    }
}