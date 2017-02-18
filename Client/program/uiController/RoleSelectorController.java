package program.uiController;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import program.ClientMain;
import program.connection.ServerManager;

public class RoleSelectorController implements AppBinder{
	ClientMain main;
	ServerManager serverManager;
	
	@FXML 
	private Button selectStudent;
	
	@FXML 
	private Button selectLecturer;
	
	@FXML
	private void initialize(){
		selectStudent.setOnAction(
				e -> loadStudent());
		selectLecturer.setOnAction(
				e -> loadLecturer());
	}
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
	}
	
	private void loadStudent(){
		
		/* sends information to the server that my role is "student",
		 	so that the server can categorize the connection
		*/
		serverManager = main.getServerManager();
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "AssignRole");
			obj.put("Role", "Student");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serverManager.sendJSON(obj);
		
		// information has been sent, load next part of UI.
		main.loadUI("ui/StudentLectureSelector.fxml");
	}
	
	private void loadLecturer(){
		serverManager = main.getServerManager();
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "AssignRole");
			obj.put("Role", "Lecturer");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serverManager.sendJSON(obj);
		
		// information has been sent, load next part of UI.
	}
	
	

}
