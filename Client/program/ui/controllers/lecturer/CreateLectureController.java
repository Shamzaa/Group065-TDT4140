package program.ui.controllers.lecturer;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import program.ClientMain;
import program.connection.ServerRequests;
import program.ui.controllers.AppBinder;

/**
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class CreateLectureController implements AppBinder{
	
	private ClientMain main;						//References the ClientMain that runs the program 
	
	@FXML private TextField lectureNameField; 		//User writes the name for a lecture here
	@FXML private Label errorLabel;					//Shows up when input is wrong
	@FXML private Button newLectureButton;			//Initializes lecture creation
	
	
	/**
	 * inits view
	 */
	@FXML
	private void initialize(){
		errorLabel.setText("");
		//TODO Later this should fetch all available lecture codes from the database table 'subjects'
		
		newLectureButton.setOnAction(    //TODO: make lecture name an input to this
				e -> createNewLecture(
						main.getClassID(),
						lectureNameField.getText()));
	}
	

	/**
	 * Method to create a new lecture. Tells the server about this
	 * so that other students can connect to it, and save info in the database
	 * @param lectureID the class code the lecturer wants to create a lecture in
	 * @param lectureName the name the lecturer wants to give the lecture
	 */
	private void createNewLecture(String lectureID, String lectureName){
		// future build: check if proposed lecture ID is in the database.

		if(lectureID.equals("")){
			//TODO this code is never reached?
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
		main.loadUI("ui/fxml/LecturerWindow.fxml");
	}
	// Interface inheritance
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#setMainApp(program.ClientMain)
	 */
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
		//TODO this print is wrong: System.out.println("Choicebox initialized");
		main.getRootController().setTitle("Create Lecture");
	}
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#closeController()
	 */
	@Override
	public void closeController(){
		//TODO make sure all threads and such is closed
	}
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#localBackChanges()
	 */
	@Override
	public void localBackChanges() {
		//UNUSED in this window at the moment		
	}
}
