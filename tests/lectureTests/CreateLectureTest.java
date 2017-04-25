package lectureTests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import program.ClientMain;
import program.uiController.CreateLectureController;
import program.uiController.StudentWindowController;

public class CreateLectureTest extends GuiTest {	
	static ClientMain main;
	static CreateLectureController c;
	
	protected Parent getRootNode() {
		// only purpose of this test is to see gui work as intended, server logic is thrown out the window
		// needs server running so we have something to send data to via socket.
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/CreateLecture.fxml"));
			
			main.startConnection("");
			//c.setMainApp(main);
			Parent p = l.load();
			c = l.getController();
			c.setMainApp(main);
			main.setClassID("asd");
			return p;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*@Before
	public void setUp(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "EndLecture");
			main.getServerManager().sendJSON(obj);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	@Test
	public void errorMessage() throws Exception {
		//sleep(10000);
		click("#lectureNameField").type("A test lecture");
		click("#newLectureButton");
		sleep(100);
		//assertEquals("There is already a lecture going on using that lecture code!", ((Label)find("#errorLabel")).getText());
		//fail("Unsure how to test this part, implement later?");
	}
	
	/*@AfterClass
	public static void tearDownClass(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "EndLecture");
			main.getServerManager().sendJSON(obj);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
