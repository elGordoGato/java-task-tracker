package managers.taskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void setUp(){
        testTaskManager = new InMemoryTaskManager();
    }


}