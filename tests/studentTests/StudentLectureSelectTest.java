package studentTests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import program.ClientMain;
import program.ui.controllers.student.LectureSelectorController;

public class StudentLectureSelectTest extends GuiTest{
	ClientMain main;
	LectureSelectorController c;
	
	protected Parent getRootNode() {
		// only purpose of this test is to see gui work as intended, server logic is thrown out the window
		// needs server running so we have something to send data to via socket.
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/fxml/StudentLectureSelector.fxml"));
			
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
	public void tryToJoinLectureError(){
		// test with just a server running and no running lectures.
		clickOn("#ConnectButton");
		@SuppressWarnings("unchecked")
		String errorExpect = "No lecture is being held with course code: " + ((ChoiceBox<String>)find("#courseCodeChoiceBox")).getSelectionModel().getSelectedItem();
		assertEquals(errorExpect, ((Label) find("#ErrorLabel")).getText());

	}
	
	@Test
	public void sendLectureCodeJoined(){
		// will try to send current class joined to server
		// won't matter if it's valid or not for this test, as the code will only be reached when a valid class has been selected
		// the method is public only for testing purposes
		main.setClassID("asd");
		//invisible statement c.joinLectureNotify();
		
	}

}
