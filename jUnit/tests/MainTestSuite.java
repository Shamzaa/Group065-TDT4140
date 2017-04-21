package tests;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.sun.net.httpserver.Authenticator.Success;

@RunWith(Suite.class)
@SuiteClasses({
	ControllerTest.class,	//Test case for fxml controllers
	DatabaseTest.class		//Test case for database functions
})

public class MainTestSuite {
	//This is empty, but needs to be here for the JUnit test to run normally
}
