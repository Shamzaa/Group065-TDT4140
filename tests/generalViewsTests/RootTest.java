package generalViewsTests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import program.ClientMain;
import program.ui.controllers.RootController;

public class RootTest extends GuiTest {
	static ClientMain main;
	static RootController c;
	
	protected Parent getRootNode() {
		// only purpose of this test is to see gui work as intended, server logic is thrown out the window
		// needs server running so we have something to send data to via socket.
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/Root.fxml"));
			
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
	public void addingPaths() throws Exception {
		assertTrue(c.viewBackButton().isDisabled());
		c.addNewPath("page1");
		c.addNewPath("page2");
		c.addNewPath("page3");
		assertFalse(c.viewBackButton().isDisabled());
	}
	/*@Test TODO causes issues with all other tests
	public void goingBack() throws Exception {
		//RootController c_2 = main.getRootController();
		main.loadUI("ui/RoleSelector.fxml");
		main.loadUI("ui/LectureLogin.fxml");
		c.viewBackButton().fire();
	}*/
}
