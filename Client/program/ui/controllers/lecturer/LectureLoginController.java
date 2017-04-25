package program.ui.controllers.lecturer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
public class LectureLoginController implements AppBinder{
	
	ClientMain main;
	
	@FXML TextField classField;
	@FXML Button loginButton;
	@FXML ChoiceBox<String> classChoiceBox;	//User chooses a subject code here
	@FXML Label errorLabel;
	
	/**
	 * inits view
	 */
	@FXML
	private void initialize(){
		errorLabel.setText("");
		loginButton.setOnAction(e -> login(classChoiceBox.getValue()));
	}
	
	
	
	/**
	 * Method to let the lecturer login to subject overview page
	 * @param classID the classcode of the subject
	 */
	private void login(String classID){
		// future build: Ability to log in individual lecturers, so there can be more lectures
		// 				 in any subject
		main.setClassID(classID);
		
		// no errors, proceed to create the lecture on the server side
		main.loadUI("ui/fxml/LecturesOverview.fxml");
	}
		

	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#setMainApp(program.ClientMain)
	 */
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		classChoiceBox.setItems(ServerRequests.getAllSubjectCodes(main.getServerManager()));
		classChoiceBox.getSelectionModel().selectFirst();

		main.getRootController().setTitle("Login");
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
