package suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import generalViewsTests.RoleSelectTest;
import generalViewsTests.RootTest;
import studentTests.StudentLectureSelectTest;
import lectureTests.CreateLectureTest;
import lectureTests.LectureLoginTest;
import lectureTests.LectureOverviewTest;
import lectureTests.LectureReviewTest;
import lectureTests.LecturerWindowTest;
import serverTests.DatabaseTests;
import studentTests.StudentWindowTest;

@RunWith(Suite.class)

// suite with all the junit tests
// OBS: main.DEBUG must be true for some of these tests to work!
@Suite.SuiteClasses({
	//- code tests ---------------------------------------------------------------------------------------
	DatabaseTests.class,
	//----------------------------------------------------------------------------------------------------
	//- GUI tests ----------------------------------------------------------------------------------------
	// needs server running to work.
	// OBS: DO NOT change the order on these, it causes some bugs because of the different threads running
	// changing the order can result in all the tests crashing!!!
	CreateLectureTest.class,
	RoleSelectTest.class,
	StudentLectureSelectTest.class,
	LectureLoginTest.class,
	LectureOverviewTest.class,
	LectureReviewTest.class,
	StudentWindowTest.class,
	RootTest.class,
	LecturerWindowTest.class
})

public class TestSuite {   
}  	
