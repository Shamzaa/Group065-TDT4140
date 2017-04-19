package program;
import java.io.IOException;

import database.Database;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import program.connection.*;
import program.ui.controllers.AppBinder;
import program.ui.controllers.RootController;


public class ClientMain extends Application{
	//If TRUE, this tells other controllers to save and return more info 
	public final boolean DEBUG = true;
	
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
	
	public void startConnection(String serverAdress){
		serverManager = new ServerManager(serverAdress, 2222);
	}
	
	public static void main(String[] args){
		launch(ClientMain.class, args);

	}
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
		loaderR.setLocation(ClientMain.class.getResource("ui/Root.fxml"));
		root = (BorderPane) loaderR.load();
		rootCont = loaderR.getController();
		rootCont.setMainApp(this);

		// Show the scene containing the root layout.
		Scene scene = new Scene(root);

		//String css = ClientMain.class.getResource("Buttons.css").toExternalForm();
		//String css = ClientMain.class.getResource("styleSheet.css").toExternalForm();
		String css = ClientMain.class.getResource("ui/Buttons.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.show();
		loadUI("ui/RoleSelector.fxml");
		
		
		
	}
	//Runs when window is closed
	@Override
	public void stop() throws Exception{
		System.out.println("Window closed....");
		//TODO add code for proper disconnection
	}
	
	public ServerManager getServerManager(){
		return serverManager;
	}
	
	public void setClassID(String classID){
		this.classID = classID;
	}
	public String getClassID(){
		return classID;
	}
	public void setLectureName(String lectureName) {
		this.lectureName = lectureName;
	}
	public String getLectureName() {
		return lectureName;
	}
	public void setLectureID(int lectureID){
		this.lectureID = lectureID;
	}
	
	public int getLectureID(){
		return lectureID;
	}
	
	public Alert viewAlert(){
		return alert;
	}
	
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
	
	public long getLostMeTimerLenght() {
		return lostMeTimerLenght;
	}
	
	public AppBinder getActiveController(){
		return loader.getController();
	}
}
