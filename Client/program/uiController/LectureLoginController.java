package program.uiController;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import program.ClientMain;
import program.connection.ServerRequests;

public class LectureLoginController implements AppBinder{
	
	ClientMain main;
	
	@FXML TextField classField;
	@FXML Button loginButton;
	@FXML ChoiceBox<String> classChoiceBox;	//User chooses a subject code here
	@FXML Label errorLabel;
	
	@FXML
	private void initialize(){
		errorLabel.setText("");
		loginButton.setOnAction(e -> login(classChoiceBox.getValue()));
	}
	
	
	
	private void login(String classID){
		// future build: Ability to log in individual lecturers, so there can be more lectures
		// 				 in any subject
		main.setClassID(classID);
		main.loadUI("ui/LecturesOverview.fxml");
	}
	
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		classChoiceBox.setItems(ServerRequests.getAllSubjectCodes(main.getServerManager()));
		classChoiceBox.getSelectionModel().selectFirst();

		main.getRootController().setTitle("Login");
	}
	@Override
	public void closeController() {
		// TODO Make sure all threads and such are closed
	}	
	@Override
	public void localBackChanges() {
		//UNUSED in this window at the moment		
	}
}
