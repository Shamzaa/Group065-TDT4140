package generalViewsTests;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import program.ClientMain;
import program.ui.controllers.RoleSelectorController;

import static org.junit.Assert.*;

import org.junit.Test;
import org.loadui.testfx.GuiTest;


public class RoleSelectTest extends GuiTest{


	@Override
	protected Parent getRootNode() {
		// TODO Auto-generated method stub
		try {
			//return 
			ClientMain main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/fxml/RoleSelector.fxml"));
			
			//main.startConnection("");
			//c.setMainApp(main);
			Parent p = l.load();
			RoleSelectorController c = l.getController();
			c.setMainApp(main);
			return p;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Test
	public void loadStudent(){
		clickOn("#selectStudent");
	}
	
	@Test
	public void loadLecturer(){
		clickOn("#selectLecturer");
	}
	
	@Test
	public void noServer(){
		clickOn("#serverField").type("asd");
		clickOn("#selectStudent");

        assertEquals("No server with proposed IP is currently running", ((Label) find("#errorLabel")).getText());

		clickOn("#selectLecturer");
		assertEquals("No server with proposed IP is currently running", ((Label) find("#errorLabel")).getText());

	}
	
	

}

/*
 * try {
			ClientMain main = new ClientMain();

			FXMLLoader loader = FXMLLoader.load(this.getClass().getResource("ui/RoleSelector.fxml"));
			RoleSelectorController c = loader.getController();
			c.setMainApp(main);
			return loader;
		} catch (IOException e) {
			System.err.println(e);
		}
		return null;
 */

