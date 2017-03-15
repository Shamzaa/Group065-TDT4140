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
		errorLabel.setText("");
		newLectureButton.setOnAction(    //TODO: make lecture name an input to this
				e -> createNewLecture(classField.getText(), classField.getText()));
	}
	
	

	private void createNewLecture(String lectureID, String lectureName){
		// future build: check if proposed lecture ID is in the database.

		if(lectureID.equals("")){
			errorLabel.setText("Lecture code is empty, please create a lecture with a valid lecture code!");
			return;
		}
		
		if(ServerRequests.serverHasLecture(main.getServerManager(), lectureID)){
			errorLabel.setText("There is already a lecture going on using that lecture code!");
			return;
		}
		
		// no errors, proceed to create the lecture on the server side
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "CreateNewLecture");
			obj.put("LectureName", lectureName);
			obj.put("ClassID", lectureID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		main.getServerManager().sendJSON(obj);
		
		
		
		main.setLectureID(lectureID);
		main.loadUI("ui/LecturerWindow.fxml");
		// load ui and pass on what lecture ID the lecturer will associate with, client side
		
		

	}
	
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
	}

}
