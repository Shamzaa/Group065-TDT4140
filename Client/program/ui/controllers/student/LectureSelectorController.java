package program.ui.controllers.student;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import program.ClientMain;
import program.connection.ServerRequests;
import program.ui.controllers.AppBinder;

/**
 * Controller for the student where he or she can select what subject to join a lecture in 
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 */
public class LectureSelectorController implements AppBinder{
	
	// ClientMain reference for getters.
	private ClientMain main;
	
	// UI components
	@FXML
	private ChoiceBox<String> courseCodeChoiceBox;
	@FXML
	private Button ConnectButton;
	@FXML
	private Label ErrorLabel;
	
	
	/**
	 * inits view
	 */
	@FXML
	private void initialize(){
		ErrorLabel.setText("");
		ConnectButton.setOnAction(
				e -> connectToLecture());
	}

	
	/**
	 * method to connect to the lecture in proposed subject. Checks if a lecture is happening before attempting to connect.
	 */
	private void connectToLecture(){
		if(!lectureIsHappening(courseCodeChoiceBox.getSelectionModel().getSelectedItem())){
			ErrorLabel.setText("No lecture is being held with course code: " + courseCodeChoiceBox.getSelectionModel().getSelectedItem());
			return;
		}
		
		main.setClassID(courseCodeChoiceBox.getSelectionModel().getSelectedItem());
		joinLectureNotify();
		main.loadUI("ui/fxml/StudentWindow.fxml");
		
		
		
	}
	
	/**
	 * method to return true or false to see if a lecture is happening in selected subject
	 * @param classID subject code
	 * @return bool if lecture is happening
	 */
	private boolean lectureIsHappening(String classID){
		// sends request to server to see if a lecture is happening
		return ServerRequests.serverHasLecture(main.getServerManager(), classID);
		
	}
	
	/**
	 * Method to notify the server that you have joined a lecture,
	 * used to update student count.
	 * 
	 */
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
	
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#setMainApp(program.ClientMain)
	 */
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
		courseCodeChoiceBox.setItems(ServerRequests.getAllSubjectCodes(main.getServerManager()));
		courseCodeChoiceBox.getSelectionModel().selectFirst();
		System.out.println("Choicebox initialized");

		main.getRootController().setTitle("Join lecture");
	}
	
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#closeController()
	 */
	@Override
	public void closeController() {
		// TODO Make sure all threads and such are closed	
	}
	
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#localBackChanges()
	 */
	@Override
	public void localBackChanges() {
		//UNUSED in this window at the moment		
	}
}
