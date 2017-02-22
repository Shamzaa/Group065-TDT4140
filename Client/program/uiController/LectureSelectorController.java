package program.uiController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import program.ClientMain;

public class LectureSelectorController implements AppBinder{
	
	// ClientMain reference for getters.
	private ClientMain main;
	
	// UI components
	@FXML
	TextField CourseCodeField;
	@FXML
	TextField ServerIPField;
	@FXML
	Button ConnectButton;
	
	@FXML
	private void initialize(){
		ConnectButton.setOnAction(
				e -> connectToLecture());
	}

	
	private void connectToLecture(){
		
	}
	
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
	}

}
