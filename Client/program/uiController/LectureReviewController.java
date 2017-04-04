package program.uiController;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
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
	@FXML Text LectureNameText;
	@FXML Text dateText;
	@FXML Text studPresText;
	@FXML Text startTimeText;
	@FXML Text stopTimeText;
	
	@FXML LineChart<String, Integer> lostMeLineChart;
	@FXML CategoryAxis xAxis;
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

	public void recieveLectureReview(JSONObject obj){
		System.out.println(">> RECIEVED STATS");
		System.out.println(obj);
		try {
			//Get info from JSON
			JSONObject statObj = obj.getJSONObject("JSONStats");
			JSONArray stampArr = obj.getJSONArray("StampList");
			int studentsJoined = statObj.getInt("studentsJoined");
			String name = statObj.getString("name");
			LocalTime start;
			LocalTime stop;
			
			if(stampArr.length() != 0){
				LocalTime firstStamp = LocalTime.parse(stampArr.getString(0).substring(11));
				LocalTime lastStamp = LocalTime.parse(stampArr.getString(stampArr.length()-1).substring(11));
				
				start = (statObj.getString("start")==""? 
						LocalTime.parse(statObj.getString("start").substring(11)) 
						: firstStamp);
				stop = (statObj.getString("stop")==""? 
						LocalTime.parse(statObj.getString("stop").substring(11)) 
						: lastStamp);
				
				
				ObservableList<String> stampTimes = FXCollections.observableArrayList();
				String oldStamp = "";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
				System.out.println(">> First stamp: " + firstStamp);
				System.out.println(">> Last stamp:  " + lastStamp);
				//TODO Calculate a good division for x-axis labels
				//Maybe take start and stop, divide to 10 part chunks?
				
				
				
				for (int i = 0; i < stampArr.length(); i++) {				
					LocalTime stamp = LocalTime.parse(stampArr.getString(i).substring(11));
					//They are ordered, so checking the previous stamp is enough
					String newStamp = stamp.format(formatter);
					System.out.println(newStamp); //Debug print
					if(!oldStamp.equals(newStamp)){
						stampTimes.add(newStamp);
						oldStamp = newStamp;
					}
				}
				//Platform.runLater(() -> {
					System.out.println(stampTimes); 
					xAxis.setCategories(stampTimes);
				//});
				
				
				
				
			} else {
				start = (statObj.getString("start")==""?
						LocalTime.parse(statObj.getString("start").substring(11))
					    : LocalTime.MIN);
				stop = (statObj.getString("stop")==""?
						LocalTime.parse(statObj.getString("stop").substring(11))
					    : LocalTime.MIN);
			}
			
			//Display info
			LectureNameText.setText(name);
			studPresText.setText(String.valueOf(studentsJoined));
			startTimeText.setText(start.toString());
			stopTimeText.setText(stop.toString());
			
			/* Prints for debugg
			System.out.println("STATS:");
			System.out.println("StudentsJoined: "  + String.valueOf(studentsJoined));
			System.out.println("Name:           "  + name);
			System.out.println("Start:          "  + start);
			System.out.println("Stop:           "  + stop);
			
			System.out.println("TIMESTAMPS:");*/
			
			
			//TODO Display timestamps in graph 
			
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
