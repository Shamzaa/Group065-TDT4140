package program.ui.controllers;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import program.ClientMain;
import java.net.Socket;
import program.connection.ServerManager;

/**
 * View that lets the user select what role to have when using the program
 * @author Erling Ihlen
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class RoleSelectorController implements AppBinder{
	ClientMain main;
	ServerManager serverManager;
	
	@FXML private Button selectStudent;
	@FXML private Button selectLecturer;
	@FXML private TextField serverField;
	@FXML private Label errorLabel;
	
	/**
	 * inits the view
	 */
	@FXML
	private void initialize(){
		errorLabel.setText("");
		selectStudent.setOnAction(
				e -> loadStudent());
		selectLecturer.setOnAction(
				e -> loadLecturer());
	}
	
	// different functions to load next part of UI after connection to the server and load UI
	/**
	 * Loads the student view
	 */
	private void loadStudent(){
		// does a test connection to proposed IP in the field, and returns if it is not available
		if(!hostAvailabilityCheck()){
			errorLabel.setText("No server with proposed IP is currently running");
			return;
		}
		main.startConnection(serverField.getText());
		/* sends information to the server that my role is "student",
		 	so that the server can categorize the connection
		*/
		serverManager = main.getServerManager();
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "AssignRole");
			obj.put("Role", "Student");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serverManager.sendJSON(obj);
		
		// information has been sent, load next part of UI.
		main.loadUI("ui/fxml/StudentLectureSelector.fxml");
	}
	
	/**
	 * loads the lecturer view
	 */
	private void loadLecturer(){
		// does a test connection to proposed IP in the field, and returns if it is not available
		if(!hostAvailabilityCheck()){
			errorLabel.setText("No server with proposed IP is currently running");
			return;
		}
		main.startConnection(serverField.getText());
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "AssignRole");
			obj.put("Role", "Lecturer");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		main.getServerManager().sendJSON(obj);
		
		main.loadUI("ui/fxml/LectureLogin.fxml");
	}
	
	// function to check if proposed server adress is online, returns true if able to connect, false otherwise
	/**
	 * A check to see if the server with proposed IP address is running.
	 * @return true or false if the server is available
	 */
	public boolean hostAvailabilityCheck(){ 
		// TODO: close connection after successfull test. Server keeps listening for this socket.
	    try (Socket server = new Socket(serverField.getText(), 2222)) {
	        return true;
	    } catch (IOException ex) {
	    	// ignore
	    }
	    return false;
	}
	
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#setMainApp(program.ClientMain)
	 */
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
		main.getRootController().setTitle("Select role and server");
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
