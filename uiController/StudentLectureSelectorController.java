package program.uiController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import program.ClientMain;

public class StudentLectureSelectorController implements AppBinder {
	ClientMain main;
	
	@FXML Button goButton;
	
	public void initialize(){
		System.out.println("initalizing lecture select");
		
		//TODO Bruker ikke textfields, går bare rett til eksempelviduet for studentviduet
		goButton.setOnAction(
				e -> main.loadUI("ui/StudentWindow.fxml"));
	}

	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
	}
}
