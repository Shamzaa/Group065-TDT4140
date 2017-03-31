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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import program.ClientMain;
import program.connection.LectureReciever;
import program.connection.LectureStatListener;
import program.connection.QuestionReciever;

public class LectureReviewController implements AppBinder, LectureReciever{
	
	private ClientMain main;
	ArrayList<Question> questionList = new ArrayList<>(); //Is never updated, can probably be removed! TODO
	private ExecutorService clientProcessingPool = Executors.newSingleThreadExecutor();
	private int connectedStudents = 0; //Is never updated, can probably be removed
	
	@FXML VBox QuestionContainer;
	//@FXML Arc lostMeRedArc;
	//@FXML Arc lostMeGreenArc;
	//@FXML Text lostMeRedText;
	//@FXML Text lostMeGreenText;
	
	//@FXML Text studentsConnectedText;
	
	@FXML
	private void initialize(){
		updateStudentsConnectedAmount();
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
	
	
	private void fetchLectureReview(){
		
		try {
			JSONObject obj = new JSONObject();
			obj.put("Function", "reviewLecture");
			obj.put("ClassID", main.getClassID());
			obj.put("LectureID", main.getLectureID());
			obj.put("QuestionAmount", Integer.MAX_VALUE);
			
			main.getServerManager().sendJSON(obj);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//TODO Unused!
	/*
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
	*/
	
	public void updateStudentsConnectedAmount(){
		//studentsConnectedText.setText(String.valueOf(connectedStudents) + " Students connected");
	}
	
	private void addQuestion(Question question){		
		questionList.add(question);
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/QuestionBox.fxml"));
		Platform.runLater(() -> {
			try {
			AnchorPane qPane = (AnchorPane) loader.load();
			question.setRelatedQuestionPane(qPane);
			QuestionBoxController controller = loader.getController();
			
			controller.setScoreVisible(true);
			controller.setQuestion(question);
			QuestionContainer.getChildren().add(qPane);
			
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
	}
	
	@Override
	public void setMainApp(ClientMain main){
		this.main = main;
		clientProcessingPool.submit(new LectureStatListener(main, this));
		fetchLectureReview();

		main.getRootController().setTitle("Lecture Review");
	}




	@Override
	public void recieveQuestions(JSONObject obj) {
		try {
			JSONArray objList = obj.getJSONArray("List");
			for (int i = 0; i < objList.length(); i++) {
				JSONObject part = objList.getJSONObject(i);
				String questionText = part.getString("question");
				int id = part.getInt("id");
				int rating = part.getInt("rating");
				String time = part.getString("time");
				
				addQuestion(new Question(id, questionText, time, rating));
			}
			sortQuestionsByScore();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void recieveLectureReview(JSONObject obj){
		System.out.println(">> RECIEVED STATS");
		System.out.println(obj);
		try {
			JSONArray stampArr = obj.getJSONArray("StampList");
			JSONObject statObj = obj.getJSONObject("JSONStats");
			System.out.println("STATS:");
			System.out.println("StudentsJoined: "  + String.valueOf(statObj.getInt("studentsJoined")));
			System.out.println("Name:           "  + String.valueOf(statObj.getString("name")));
			System.out.println("Start:          "  + String.valueOf(statObj.getString("start")));
			System.out.println("Stop:           "  + String.valueOf(statObj.getString("stop")));
			System.out.println("TIMESTAMPS:");
			for (int i = 0; i < stampArr.length(); i++) {
				System.out.println(stampArr.get(i));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	// not used in this view.
	@Override
	public void fetchLectures() {
		// TODO Auto-generated method stub
		
	}

	// not used in this view.
	@Override
	public void recieveLectures(JSONObject obj) {
		// TODO Auto-generated method stub
		
	}

}
