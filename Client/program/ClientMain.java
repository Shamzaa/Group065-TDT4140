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
import program.uiController.AppBinder;
import program.uiController.RootController;


public class ClientMain extends Application{
	
	// UI elements
	private Stage stage;
	private BorderPane root; //all windows are loaded into centerpane via loadUI()
	private RootController rootCont;
	private int windowMinWidht = 685;
	private int windowMinHeight = 470;
	
	// reference elements
	private ServerManager serverManager;
	private final long lostMeTimerLenght = 5; 	//Decides how long it will wait until it discards a lostMeSignal (Seconds).
	
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
			Alert alert = new Alert(AlertType.WARNING);
			System.out.println(1);
			alert.setTitle(title);
			System.out.println(2);
			alert.setHeaderText(header);
			System.out.println(3);
			alert.setContentText(content);
			System.out.println("show:");
			alert.show();
			System.out.println("Alert displayed");
		});
	}
	
	public void loadUI(String path){
		try {
			// Load overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClientMain.class.getResource(path));
			AnchorPane overview = (AnchorPane) loader.load();

			// Set overview into the center of root layout.
			root.setCenter(overview);
			
			// give controller access to main app
			AppBinder controller = loader.getController();
			controller.setMainApp(this);
			
			if(!rootCont.skipAddPath()){
				rootCont.addNewPath(path, controller);				
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
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ClientMain.class.getResource("ui/Root.fxml"));
		root = (BorderPane) loader.load();
		rootCont = loader.getController();
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
	
	public RootController getRootController(){
		return rootCont;
	}
	
	public long getLostMeTimerLenght() {
		return lostMeTimerLenght;
	}
}
