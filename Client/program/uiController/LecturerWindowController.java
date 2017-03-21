package program.uiController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import classes.Question;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	ArrayList<Question> questionList = new ArrayList<>();
	private int connectedStudents = 0;
	private int lostStudents = 0;
	private int liveLectureID;
	
	private ExecutorService clientProcessingPool = Executors.newSingleThreadExecutor();
	
	@FXML VBox QuestionContainer;
	@FXML Arc lostMeRedArc;
	@FXML Arc lostMeGreenArc;
	@FXML Text lostMeRedText;
	@FXML Text lostMeGreenText;
	
	@FXML Text studentsConnectedText;
	
	@FXML
	public void initialize(){
				
		updatePieChartValues();
		updateStudentsConnectedAmount();
	}
	public void updateStudentsConnectedAmount(){
		studentsConnectedText.setText(String.valueOf(connectedStudents) + " Students connected");
	}
	
	private void sortQuestionsByScore(){
		Platform.runLater(() -> {
			//The -1 reverses the sorting order
			questionList.sort((q1, q2)-> -1*Integer.compare(q1.getRating(), q2.getRating()));
			ObservableList<AnchorPane> workingCollection = FXCollections.observableArrayList();
			for (Question question : questionList) {
				System.out.println("changing orders");
				workingCollection.add(question.getRelatedQuestionPane());
			}
			QuestionContainer.getChildren().setAll(workingCollection);
		});	
	}
	
	public void updatePieChartValues(){
		double percentRed = 0;		
		if(connectedStudents == 0){
			percentRed = 0;
		} else {
			percentRed =100*lostStudents/connectedStudents;	
		}
		

		//System.out.println("Setting angles, ["+String.valueOf(percentRed));
		
		lostMeRedText.setText(String.valueOf(percentRed)+"%");
		lostMeGreenText.setText(String.valueOf(100 - percentRed)+"%");
		//System.out.println("text done");
		
		double newAngle = 360*(percentRed/100);
		//System.out.println("angles got");
		
		lostMeRedArc.setStartAngle(90-newAngle);
		lostMeRedArc.setLength(newAngle);
		lostMeGreenArc.setStartAngle(90);
		lostMeGreenArc.setLength(360-newAngle);
		
		//System.out.print(oldAngle);
		//System.out.print(" -> ");
		//System.out.println(newAngle);
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
	
	
	private void addQuestion(Question question){		
		questionList.add(question);
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/QuestionBox.fxml"));
		Platform.runLater(() -> {
			try {
			AnchorPane qPane = (AnchorPane) loader.load();
			/*for (Node node : qPane.getChildren()) {
				if (node.getId().equals("QuestionText")){
					((TextArea) node).setText(question);
					
					break;
				}
			}*/
			question.setRelatedQuestionPane(qPane);
			// Runs Controller functions
			QuestionBoxController controller = loader.getController();
			
			controller.setScoreVisible(true);
			controller.setQuestion(question);
			//controller.setScore(question.getRating());
			//controller.setQuestionId(question.getId());
			// Adds the questionBox ui element to QuestionContainer
			QuestionContainer.getChildren().add(qPane);
			
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
	}
	
	
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		clientProcessingPool.submit(new ClientListener(main, this));
		fetchLiveLectureID();
		
	}
	//-> Functions for QuestionReciever
	@Override
	public void fetchQuestions(int numberOfQuestions){
		JSONObject obj = new JSONObject();
		try{
			obj.put("Function", "GetLatestQuestions");
			obj.put("QuestionAmount", numberOfQuestions);
			obj.put("ClassID", main.getClassID());
			obj.put("LectureID", liveLectureID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		main.getServerManager().sendJSON(obj);
	}
	
	@Override
	public void recieveQuestions(JSONObject obj) {
		// TODO Auto-generated method stub
		//System.out.println("Recieved Messages: " + obj.toString());
		try {
			JSONArray objList = obj.getJSONArray("List");
			for (int i = 0; i < objList.length(); i++) {
				JSONObject part = objList.getJSONObject(i);
				String questionText = part.getString("question");
				int id = part.getInt("id");
				int rating = part.getInt("rating");
				String time = part.getString("time");
				
				//System.out.println("Part:     " + part);
				//System.out.println("Question: " + question);
				
				addQuestion(new Question(id, questionText, time, rating));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void fetchLiveLectureID() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "GetLiveLectureID");
			obj.put("ClassID", main.getClassID());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		main.getServerManager().sendJSON(obj);
	}
	
	@Override
	public void setLiveLectureID(int ID) {
		System.out.println("Setting liveLectureID to: " + ID);
		this.liveLectureID = ID;
	}
	@Override
	public void updateQuestionScore(int questionID, int newScore) {
		for (Question question : questionList) {
			if(question.getId() == questionID){
				question.setRating(newScore);
				break;
			}
		}
		sortQuestionsByScore();
	}
}
