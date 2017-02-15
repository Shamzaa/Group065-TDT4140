package program.uiController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import program.ClientMain;

public class RoleSelectorController implements AppBinder{
	ClientMain main;
	
	@FXML 
	private Button selectStudent;
	
	@FXML 
	private Button selectLecturer;
	
	@FXML
	private void initialize(){
		selectStudent.setOnAction(
				e -> main.loadUI("ui/StudentLectureSelector.fxml"));
	}
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
	}
	
	

}
