package program;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import program.ui.ConsoleController;

public class SCMain extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Platform.exit();
				System.exit(0);
			}
		});
		try{
			FXMLLoader l = new FXMLLoader();
			l.setLocation(SCMain.class.getResource("ui/ConsoleWindow.fxml"));
			AnchorPane p = (AnchorPane)l.load();
			Scene scene = new Scene(p);
			
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			ConsoleController cc = l.getController();
			cc.setServer();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		launch(args);
	}
	
}
