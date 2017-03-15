package program.uiController;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import program.ClientMain;
import program.connection.ServerRequests;

public class LectureSelectorController implements AppBinder{
	
	// ClientMain reference for getters.
	private ClientMain main;
	
	// UI components
	@FXML
	private TextField CourseCodeField;
	@FXML
	private Button ConnectButton;
	@FXML
	private Label ErrorLabel;
	
	
	@FXML
	private void initialize(){
		ErrorLabel.setText("");
		ConnectButton.setOnAction(
				e -> connectToLecture());
	}

	
	private void connectToLecture(){
		// TODO: check if coursecodefield is not empty, and send a request to the server to see if a lecture is happening in that class.
		if(CourseCodeField.getText().equals("")){
			ErrorLabel.setText("the field for course code is empty");
			return;
		}
		if(!lectureIsHappening(CourseCodeField.getText())){
			ErrorLabel.setText("No lecture is being held with course code: " + CourseCodeField.getText());
			return;
		}
		
		main.setClassID(CourseCodeField.getText());
		joinLectureNotify();
		main.loadUI("ui/StudentWindow.fxml");
		
		
		
	}
	
	private boolean lectureIsHappening(String classID){
		// sends request to server to see if a lecture is happening
		return ServerRequests.serverHasLecture(main.getServerManager(), classID);
		
	}
	
	private void joinLectureNotify(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "JoinLecture");
			obj.put("ClassID", main.getClassID());
			main.getServerManager().sendJSON(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
	}

}
