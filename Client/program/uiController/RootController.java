package program.uiController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class RootController {
	@FXML Button backButton;
	@FXML Text titleText;
	private String prevPage;
	private AppBinder preController;
	
	@FXML
	public void initialize(){
		System.out.println("Root controller initialized");
	}
	
}
