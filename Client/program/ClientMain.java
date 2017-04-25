package program;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import program.connection.*;
import program.ui.controllers.AppBinder;
import program.ui.controllers.RootController;


/**
 * Main class that is instantiated to run the application
 * 
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class ClientMain extends Application{
	//If TRUE, this tells other controllers to save and return more info 
	public final boolean DEBUG = false;
	
	// UI elements
	private Stage stage;
	private BorderPane root; //all windows are loaded into centerpane via loadUI()
	private RootController rootCont;
	private int windowMinWidht = 685;
	private int windowMinHeight = 470;
	
	// reference elements
	private ServerManager serverManager;
	private final long lostMeTimerLenght = 5; 	//Decides how long it will wait until it discards a lostMeSignal (Seconds).
	private Alert alert;
	
	FXMLLoader loader = null;
	
	// attributes
	private String classID;
	private String lectureName;
	private int lectureID;
	
	/** 
	 * This method sets up our ServerManager object
	 * that is responsible for managing all requests and incoming info
	 * from the server.
	 * 
	 * @param serverAdress String that is the IP address of the server
	 */
	public void startConnection(String serverAdress){
		serverManager = new ServerManager(serverAdress, 2222);
	}
	
	/**
	 * Launches the application
	 * @param args
	 */
	public static void main(String[] args){
		launch(ClientMain.class, args);

	}
	/**
	 * This method is used to alert the user of something that happened,
	 * with a popup window. In our case it's used, for instance, when
	 * the lecturer has lost a treshold of students during the lecture.
	 * 
	 * @param title Title of the window that appears
	 * @param header Title of the content in the window
	 * @param content Text that appears in the window informing the user what happened
	 */
	public void displayAlert(String title, String header, String content){
		Platform.runLater(()-> {
			System.out.println("Displaying alert!");
			alert = new Alert(AlertType.WARNING);
			alert.setTitle(title);
			alert.setHeaderText(header);
			alert.setContentText(content);
			alert.show();
		});
	}
	
	/**
	 * This method loads the view related to the fxml file in parameter,
	 * and sets it in the center window of our root window.
	 * @param path Relative url to the fxml file
	 */
	public void loadUI(String path){
		try {
			if(loader != null){
				loader.setController(null);
			}
			// Load overview.
			loader = new FXMLLoader();
			loader.setLocation(ClientMain.class.getResource(path));
			AnchorPane overview = (AnchorPane) loader.load();

			// Set overview into the center of root layout.
			root.setCenter(overview);
			
			// give controller access to main app
			AppBinder controller = loader.getController();
			controller.setMainApp(this);
			
			if(!rootCont.skipAddPath()){
				rootCont.addNewPath(path);				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// starts server connection
		// on exit handling
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Platform.exit();
				System.exit(0);
			}
		});
		
		// loads ui
		this.stage = primaryStage;
		stage.setTitle("HearMe");
		stage.setMinWidth(windowMinWidht);
		stage.setMinHeight(windowMinHeight);
		
		FXMLLoader loaderR = new FXMLLoader();
		loaderR.setLocation(ClientMain.class.getResource("ui/fxml/Root.fxml"));
		root = (BorderPane) loaderR.load();
		rootCont = loaderR.getController();
		rootCont.setMainApp(this);

		// Show the scene containing the root layout.
		Scene scene = new Scene(root);

		//String css = ClientMain.class.getResource("Buttons.css").toExternalForm();
		//String css = ClientMain.class.getResource("styleSheet.css").toExternalForm();
		String css = ClientMain.class.getResource("ui/fxml/Buttons.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.show();
		loadUI("ui/fxml/RoleSelector.fxml");
		
		
		
	}
	/* (non-Javadoc)
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception{
		System.out.println("Window closed....");
	}
	
	/**
	 * Getter to get the servermanager.
	 * @return manager to handle requests and info from server.
	 */
	public ServerManager getServerManager(){
		return serverManager;
	}
	
	/**
	 * Sets the class code the client is connected to.
	 * @param classID class code for the subject the client is connected to
	 */
	public void setClassID(String classID){
		this.classID = classID;
	}
	/**
	 * getter to get the class code the client is connected to.
	 * @return Class code
	 */
	public String getClassID(){
		return classID;
	}
	
	/**
	 * sets the name of the lecture the client is connected to.
	 * @param lectureName name of the lecture.
	 */
	public void setLectureName(String lectureName) {
		this.lectureName = lectureName;
	}
	/**
	 * getter to get the name of the lecture the client is connected to
	 * @return name of the lecture
	 */
	public String getLectureName() {
		return lectureName;
	}
	/**
	 * sets the ID of the lecture the client is connected to.
	 * This is used to know which lecture in the database info needs to be parsed to,
	 * as well as for the server to know which clients info and notifications
	 * should be sent to
	 * @param lectureID ID of the lecture
	 */
	public void setLectureID(int lectureID){
		this.lectureID = lectureID;
	}
	
	/**
	 * getter to get the lecture ID the client is connected to
	 * @return id of the lecture
	 */
	public int getLectureID(){
		return lectureID;
	}
	
	public Alert viewAlert(){
		return alert;
	}
	/**
	 * Getter to get the controller of the root window.
	 * @return root controller
	 */
	public RootController getRootController(){
		if(rootCont == null){
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClientMain.class.getResource("ui/Root.fxml"));
			try {
				root = (BorderPane) loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rootCont = loader.getController();
			rootCont.setMainApp(this);
		}
		return rootCont;
	}
	
	/**
	 * Method used to get the length of the timer to controll the cooldown of
	 * "you lost me" feature
	 * @return lost-me timer
	 */
	public long getLostMeTimerLenght() {
		return lostMeTimerLenght;
	}
	
	/**
	 * Method used to get the controller that is currently active in the 
	 * main window of the root window. Used to make the back button function properly
	 * @return controller of active window.
	 */
	public AppBinder getActiveController(){
		return loader.getController();
	}
}
