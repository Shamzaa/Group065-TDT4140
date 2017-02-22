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
		main.loadUI("ui/StudentWindow.fxml");
		// TODO: lecture is running, change ui to student view, where they can make queries for questions, and send "you lost me"

		
	}
	
	private boolean lectureIsHappening(String classID){
		// sends request to server to see if a lecture is happening
		return ServerRequests.serverHasClass(main.getServerManager(), classID);
		
	}
	
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
	}

}
