import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import program.ClientMain;
import program.uiController.LectureSelectorController;
import program.uiController.RoleSelectorController;
import program.uiController.StudentWindowController;

public class StudentWindowTest extends GuiTest{
	ClientMain main;
	StudentWindowController c;
	
	protected Parent getRootNode() {
		// only purpose of this test is to see gui work as intended, server logic is thrown out the window
		// needs server running so we have something to send data to via socket.
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/StudentWindow.fxml"));
			
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

	@Test
	public void openQuestionWindow(){
		clickOn("#questionButton");
		assertEquals(true, ((GridPane) find("#askQuestionContainer")).isVisible());

	}
	
	@Test
	public void youLostMe(){
		clickOn("#lostMeButton");
	}
	
	@Test
	public void sendQuestion(){

		clickOn("#questionButton");
		TextArea t = (TextArea) find("#askQuestionTextField");
		clickOn("#askQuestionTextField").type("a Q");
		assertEquals("a Q", t.getText());

		clickOn("#submitQuestionButton");
	}
	
	@Test
	public void addQuestion(){
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
		c.updateQuestionScore(158, 10);
	}

}
