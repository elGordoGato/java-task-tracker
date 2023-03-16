package managers.taskManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void setUp(){
        testTaskManager = Managers.getDefault();
    }


}