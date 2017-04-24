package lectureTests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import program.ClientMain;
import program.uiController.LectureLoginController;
import program.uiController.LectureOverviewController;

public class LectureOverviewTest extends GuiTest {
	static ClientMain main;
	static LectureOverviewController c;
	
	@Override
	protected Parent getRootNode() {
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/LecturesOverview.fxml"));
			
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
	public void recieveLecturesAndLoad() throws Exception {
		String recievedString = "{Function:addLectures,List:[{studentsJoined:300,date:19-04-2017,lectureName:A FAKE LECTURE,id:1000}]}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectures(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VBox vb = (VBox) find("#lectureContainer");
		sleep(100);
		assertEquals(1, vb.getChildren().size());
		clickOn(c.getLectureBoxController(0).viewButton());
	}
	
	@Test
	public void createNewLecture() throws Exception {
		clickOn("#createLectureButton");
	}
}
