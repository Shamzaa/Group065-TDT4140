package program.uiController;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import program.ClientMain;
import program.connection.ServerRequests;

public class LectureLoginController implements AppBinder{
	
	ClientMain main;
	
	@FXML TextField classField;
	@FXML Button loginButton;
	@FXML Label errorLabel;
	
	@FXML
	private void initialize(){
		errorLabel.setText("");
		loginButton.setOnAction(e -> login(classField.getText()));
	}
	
	
	
	private void login(String classID){
		// future build: check if proposed class ID is in the database.

		if(classID.equals("")){
			errorLabel.setText("Class code is empty, please create a lecture with a valid class code!");
			return;
		}
		
		main.setClassID(classID);
		
		
		// no errors, proceed to create the lecture on the server side
		main.loadUI("ui/LecturesOverview.fxml");
		

	}
	

	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
	}

}
