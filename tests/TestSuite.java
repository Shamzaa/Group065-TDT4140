import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

// suite with all the junit tests
@Suite.SuiteClasses({
	// needs server running to work.
   RoleSelectTest.class,
   StudentLectureSelectTest.class,
   StudentWindowTest.class
})

public class TestSuite {   
}  	