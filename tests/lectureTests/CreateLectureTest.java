package lectureTests;

import java.io.IOException;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import program.ClientMain;
import program.ui.controllers.lecturer.CreateLectureController;

public class CreateLectureTest extends GuiTest {	
	static ClientMain main;
	static CreateLectureController c;
	
	protected Parent getRootNode() {
		// only purpose of this test is to see gui work as intended, server logic is thrown out the window
		// needs server running so we have something to send data to via socket.
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/fxml/CreateLecture.fxml"));
			
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
	public void errorMessage() throws Exception {
		//sleep(10000);
		clickOn("#lectureNameField").type("A test lecture");
		clickOn("#newLectureButton");
		sleep(100);
		//assertEquals("There is already a lecture going on using that lecture code!", ((Label)find("#errorLabel")).getText());
		//fail("Unsure how to test this part, implement later?");
	}
}
