package program.uiController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import program.ClientMain;
import program.connection.ClientConnection;
import program.connection.ClientListener;
import program.connection.QuestionReciever;

public class LecturerWindowController implements AppBinder, QuestionReciever {
	ClientMain main;
	ArrayList<String> questionList = new ArrayList<>();
	private int connectedStudents = 0;
	private int lostStudents = 0;
	
	private ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
	
	@FXML VBox QuestionContainer;
	@FXML Arc lostMeRedArc;
	@FXML Arc lostMeGreenArc;
	@FXML Text lostMeRedText;
	@FXML Text lostMeGreenText;
	
	@FXML Text studentsConnectedText;
	
	@FXML
	public void initialize(){
		
		
		addQuestion("TEST: monotonectally administrate leveraged initiatives");
		addQuestion("TEST: interactively envisioneer reliable e-markets conveniently plagiarize reliable synergy");
		addQuestion("TEST: continually reconceptualize one-to-one niches conveniently reinvent maintainable testing procedures uniquely repurpose&#10;customer directed virtualization");
		addQuestion("TEST: 1");
		addQuestion("TEST: 2");
		addQuestion("TEST: 3");
		addQuestion("TEST: 4");
		addQuestion("TEST: 5");
		addQuestion("TEST: 6");
		addQuestion("TEST: 7");
		addQuestion("TEST: 8");
		
		updatePieChartValues();
		updateStudentsConnectedAmount();
		
	}
	public void updateStudentsConnectedAmount(){
		studentsConnectedText.setText(String.valueOf(connectedStudents) + " Students connected");
	}
	
	public void updatePieChartValues(){
		double percentRed = 0;		
		if(connectedStudents == 0){
			percentRed = 0;
		} else {
			percentRed =100*lostStudents/connectedStudents;	
		}
		

		System.out.println("Setting angles, ["+String.valueOf(percentRed));
		
		lostMeRedText.setText(String.valueOf(percentRed)+"%");
		lostMeGreenText.setText(String.valueOf(100 - percentRed)+"%");
		System.out.println("text done");
		
		double oldAngle = lostMeRedArc.getLength();
		double newAngle = 360*(percentRed/100);
		System.out.println("angles got");
		
		lostMeRedArc.setStartAngle(90-newAngle);
		lostMeRedArc.setLength(newAngle);
		lostMeGreenArc.setStartAngle(90);
		lostMeGreenArc.setLength(360-newAngle);
		
		System.out.print(oldAngle);
		System.out.print(" -> ");
		System.out.println(newAngle);
	}
	
	public void studentJoined(){
		connectedStudents++;
		updateStudentsConnectedAmount();
		updatePieChartValues();
	}
	
	public void studentLost(){
		lostStudents ++;
		updatePieChartValues();
	}
	
	
	public void addQuestion(String question){		
		questionList.add(question);
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/QuestionBox.fxml"));
		try {
			
			AnchorPane qPane = (AnchorPane) loader.load();
			for (Node node : qPane.getChildren()) {
				if (node.getId().equals("QuestionText")){
					((TextArea) node).setText(question);
					
					/* TODO add later. Automatisk justere høyden til boksen for å passe tekstlengden
					Text helper = new Text();
					helper.setText(text);
				    helper.setFont(font);
				    helper.setWrappingWidth((int)wrappingWidth);
				    helper.getLayoutBounds().getHeight();
					*/
					break;
				}
			}
			//Kan bruke add(index, node) for rekkefølge
			QuestionContainer.getChildren().add(qPane);
			QuestionContainer.getChildren().add(new Separator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		clientProcessingPool.submit(new ClientListener(main, this));
	}
	//- Functions for QuestionReciever
	@Override
	public void fetchQuestions(int numberOfQuestions) {
		// TODO Auto-generated method stub
		System.out.println("Fetching questiosn");
	}
	@Override
	public void recieveQuestions(JSONObject obj) {
		// TODO Auto-generated method stub
		System.out.println("recieving questions");
		
	}		
}
