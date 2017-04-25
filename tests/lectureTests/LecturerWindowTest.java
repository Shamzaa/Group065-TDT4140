package lectureTests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import program.ClientMain;
import program.ui.controllers.lecturer.LecturerWindowController;

public class LecturerWindowTest extends GuiTest {
	static ClientMain main;
	static LecturerWindowController c;
	
	@Override
	protected Parent getRootNode() {
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/LecturerWindow.fxml"));
			
			main.startConnection("");
			//c.setMainApp(main);
			Parent p = l.load();
			c = l.getController();
			c.setMainApp(main);
			return p;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Test
	public void recieveQuestions() throws Exception {
		// Try adding a string to the question list
		String recievedString = "{\"Function\":\"addQuestions\",\"QuestionAmount\":2147483647,\"List\":[{\"question\":\"dsa\",\"rating\":\"0\",\"time\":\"2017-04-21 02:37:37.0\",\"id\":\"158\"},{\"question\":\"asd\",\"rating\":\"0\",\"time\":\"2017-04-21 02:37:31.0\",\"id\":\"157\"}]}";
		recievedString.replaceAll("\\/","");
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveQuestions(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VBox vb = (VBox) find("#QuestionContainer");
		//Confirm that there are two children now
		sleep(200); // Needs to wait a couple of seconds, because of the nature of runLater() 
		assertEquals(2, vb.getChildren().size());
		// If the questions where added correctly, this will not crash
		c.updateQuestionScore(158, 10);
	}
	
	@Test
	public void studentJoined() throws Exception{
		assertEquals("0 Students connected", ((Text) find("#studentsConnectedText")).getText());
		c.studentJoined();
		c.studentJoined();
		assertEquals("2 Students connected", ((Text) find("#studentsConnectedText")).getText());
	}
	
	@Test
	public void studentLost() throws Exception {
		for (int i = 0; i < 10; i++) {
			c.studentJoined();			
		}
		c.studentLost();
		assertEquals("10.0%", ((Text) find("#lostMeRedText")).getText());
		
		sleep(1000);
		for (int i = 0; i < 9; i++) {
			c.studentLost();			
		}
		sleep(100);
		assertTrue(main.viewAlert().isShowing());
	}
	
	@AfterClass
	public static void tearDownClass(){
		c.closeController();
	}
}
