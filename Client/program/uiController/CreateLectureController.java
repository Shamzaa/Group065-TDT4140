package program.uiController;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import program.ClientMain;
import program.connection.ServerRequests;

public class CreateLectureController implements AppBinder{
	
	private ClientMain main;
	
	@FXML
	private TextField classField;
	@FXML
	private Label errorLabel;
	@FXML
	private Button newLectureButton;
	
	
	@FXML
	private void initialize(){
		newLectureButton.setOnAction(
				e -> createNewLecture(classField.getText()));
	}
	
	

	private void createNewLecture(String classID){
		// future build: check if proposed class ID is in the database.

		if(classID.equals("")){
			errorLabel.setText("Class code is empty, please create a lecture with a valid class code!");
			return;
		}
		
		if(ServerRequests.serverHasClass(main.getServerManager(), classID)){
			errorLabel.setText("There is already a lecture going on using that class code!");
			return;
		}
		
		// no errors, proceed to create the lecture on the server side
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "CreateNewLecture");
			obj.put("ClassID", classID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		main.getServerManager().sendJSON(obj);
		
		
		// load ui and pass on what class ID the lecturer will associate with, client side
		
		

	}
	
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
	}

}
