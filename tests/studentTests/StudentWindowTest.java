package studentTests;
import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import program.ClientMain;
import program.ui.controllers.QuestionBoxController;
import program.ui.controllers.student.StudentWindowController;

public class StudentWindowTest extends GuiTest{
	ClientMain main;
	static StudentWindowController c;
	
	protected Parent getRootNode() {
		// only purpose of this test is to see gui work as intended, server logic is thrown out the window
		// needs server running so we have something to send data to via socket.
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/fxml/StudentWindow.fxml"));
			
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
		long waitTime = main.getLostMeTimerLenght()*1000;
		Button b = find("#lostMeButton");
		clickOn("#lostMeButton");
		assertEquals("Notification sent!", b.getText());
		sleep(waitTime);
		assertEquals("I AM LOST!", b.getText());
	}
	
	@Test
	public void charLimits(){
		clickOn("#questionButton");
		TextArea t = (TextArea) find("#askQuestionTextField");
		Button b = (Button) find("#submitQuestionButton");
		KeyCode kc = KeyCode.BACK_SPACE; 
		
		assertTrue(b.isDisabled());
		clickOn(t).type("s");
		assertFalse(b.isDisabled());
		press(kc);
		release(kc);
		assertTrue(b.isDisabled());
		
		for (int i = 0; i < 14; i++) {
			t.setText(t.getText() + "1234567890");
		}
		type("a");
		assertTrue(b.isDisabled());
		press(kc);
		release(kc);
		assertFalse(b.isDisabled());
		
	}
	
	@Test
	public void sendQuestion(){
		clickOn("#questionButton");
		GridPane gp = ((GridPane) find("#askQuestionContainer"));
		TextArea t = (TextArea) find("#askQuestionTextField");
				
		clickOn("#askQuestionTextField").type("a Q");
		assertEquals("a Q", t.getText());
		clickOn("#submitQuestionButton");
		assertFalse(gp.isVisible());
		
	}
	
	@Test
	public void addQuestion(){
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
	public void voteQuestion(){
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
		sleep(200);
		QuestionBoxController qbc = c.getQuestionBoxController(0);
		qbc.voteQuestion("good");
		c.updateQuestionScore(158, 10);
	}
	
	@Test
	public void localBackChanges(){
		//Tests the localBackChanges method closes the input overlay, and also removes text
		clickOn("#questionButton");
		
		GridPane gp = ((GridPane) find("#askQuestionContainer"));
		TextArea t = (TextArea) find("#askQuestionTextField");
		
		assertTrue(gp.isVisible());
		c.localBackChanges();
		assertFalse(gp.isVisible());
		
		clickOn("#questionButton");
		clickOn("#askQuestionTextField").type("test");
		c.localBackChanges();
		clickOn("#questionButton");
		assertEquals("", t.getText());
	}
	
	/*@AfterClass
	public static void tearDown() throws Exception{
		invokeAndWait(() -> {			
			c.closeController();
		});
	}*/
}
