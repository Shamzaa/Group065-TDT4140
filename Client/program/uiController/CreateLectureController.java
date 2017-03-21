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

public class CreateLectureController implements AppBinder{
	
	private ClientMain main;						//References the ClientMain that runs the program 
	
	@FXML private TextField lectureNameField; 		//User writes the name for a lecture here
	@FXML private ChoiceBox<String> classChoiceBox;	//User chooses a subject code here
	@FXML private Label errorLabel;					//Shows up when input is wrong
	@FXML private Button newLectureButton;			//Initializes lecture creation
	
	
	@FXML
	private void initialize(){
		errorLabel.setText("");
		//TODO Later this should fetch all available lecture codes from the database table 'subjects'
		
		newLectureButton.setOnAction(    //TODO: make lecture name an input to this
				e -> createNewLecture(
						classChoiceBox.getSelectionModel().getSelectedItem(),
						lectureNameField.getText()));
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
		
		
		
		System.out.println("Set classID to "+ String.valueOf(lectureID));
		// load ui and pass on what lecture ID the lecturer will associate with, client side
		main.setClassID(lectureID);
		main.setLectureName(lectureName);
		main.loadUI("ui/LecturerWindow.fxml");
	}
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
		classChoiceBox.setItems(ServerRequests.getAllSubjectCodes(main.getServerManager()));
		classChoiceBox.getSelectionModel().selectFirst();
		System.out.println("Choicebox initialized");
	}

}
