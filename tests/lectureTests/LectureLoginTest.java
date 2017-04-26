package lectureTests;

import java.io.IOException;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import program.ClientMain;
import program.ui.controllers.lecturer.LectureLoginController;

public class LectureLoginTest extends GuiTest {
	static ClientMain main;
	static LectureLoginController c;
	
	@Override
	protected Parent getRootNode() {
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/fxml/LectureLogin.fxml"));
			
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
	public void login() {
		@SuppressWarnings("unchecked")
		ChoiceBox<String> cb = (ChoiceBox<String>) find("#classChoiceBox");
		clickOn(cb);
		moveBy(0, 20);
		clickOn();
		clickOn("#loginButton");
	}
}
