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
	
	
	@FXML
	private void initialize(){
		ErrorLabel.setText("");
		ConnectButton.setOnAction(
				e -> connectToLecture());
	}

	
	private void connectToLecture(){
		if(!lectureIsHappening(courseCodeChoiceBox.getSelectionModel().getSelectedItem())){
			ErrorLabel.setText("No lecture is being held with course code: " + courseCodeChoiceBox.getSelectionModel().getSelectedItem());
			return;
		}
		
		main.setClassID(courseCodeChoiceBox.getSelectionModel().getSelectedItem());
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
	
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
		courseCodeChoiceBox.setItems(ServerRequests.getAllSubjectCodes(main.getServerManager()));
		courseCodeChoiceBox.getSelectionModel().selectFirst();
		System.out.println("Choicebox initialized");

		main.getRootController().setTitle("Join lecture");
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
