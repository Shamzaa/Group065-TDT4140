package program.uiController;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
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
import javafx.scene.chart.XYChart;
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
	ArrayList<Question> questionList = new ArrayList<>();
	private ExecutorService clientProcessingPool = Executors.newSingleThreadExecutor();
	
	@FXML VBox QuestionContainer;
	@FXML Text LectureNameText;
	@FXML Text dateText;
	@FXML Text studPresText;
	@FXML Text startTimeText;
	@FXML Text stopTimeText;
	
	@FXML BarChart<String, Integer> lostMeBarChart; 
	@FXML CategoryAxis xAxis;
	
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
			e.printStackTrace();
		}
	}
	
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
	
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	@Override
	public void setMainApp(ClientMain main){
		this.main = main;
		clientProcessingPool.submit(new LectureStatListener(main, this, 2));
		fetchLectureReview();
		
		main.getRootController().setTitle("Lecture Review");
	}
	
	@Override
	public void closeController() {
		// no threads to close here.
		
	}

	//-> From LectureReciever
	@Override
	public void recieveQuestions(JSONObject obj) {
		System.out.println("Review recieved questions");
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
			// Time.valueOf(statObj.getString("start")).toLocalTime();
			LocalTime start;
			LocalTime stop;

			//Decide start and stop timeStamps, if present
			if(stampArr.length() != 0){
				LocalTime firstStamp = LocalTime.parse(stampArr.getString(0).substring(11));
				LocalTime lastStamp = LocalTime.parse(stampArr.getString(stampArr.length()-1).substring(11));
				start = (!statObj.getString("start").equals("")? 
						Time.valueOf(statObj.getString("start")).toLocalTime() 
						: firstStamp);
				stop = (!statObj.getString("stop").equals("")? 
						Time.valueOf(statObj.getString("stop")).toLocalTime() 
						: lastStamp);
			} 
			else {
				start = (!statObj.getString("start").equals("")?
						Time.valueOf(statObj.getString("start")).toLocalTime()
					    : LocalTime.MIN);
				stop = (!statObj.getString("stop").equals("")?
						Time.valueOf(statObj.getString("stop")).toLocalTime()
					    : LocalTime.MIN);
			}
			//Set up the graph categories
			ObservableList<String> stampTimes = FXCollections.observableArrayList();
			
			int categoryDiff = 10; //increase this for more graph categories
			long diffHours = ChronoUnit.HOURS.between(start, stop);
			long diffMin = ChronoUnit.MINUTES.between(start, stop);
			long diffSec = ChronoUnit.SECONDS.between(start, stop);
			
			System.out.println(diffHours);
			System.out.println(diffMin);
			System.out.println(diffSec);
			
			String formatPattern;
			DateTimeFormatter formatter;
			if(diffHours > 5) {
				//Graph by hours
				formatPattern = "HH";
				formatter = DateTimeFormatter.ofPattern(formatPattern);
				LocalTime tempTime = start.withSecond(0);
				long hoursToAdd = (diffHours/categoryDiff > 0 ? diffHours/categoryDiff : 1);
				
				//System.out.println(hoursToAdd);
				
				stampTimes.add(tempTime.format(formatter));
				while (tempTime.isBefore(stop)) {
					tempTime = tempTime.plusHours(hoursToAdd);
					stampTimes.add(tempTime.format(formatter));
					//System.out.println("Temptime: " + tempTime.format(formatter));
				}
			}
			else if (diffMin > 10) {
				//Graph by minutes
				formatPattern = "hh:mm";
				formatter = DateTimeFormatter.ofPattern(formatPattern);
				LocalTime tempTime = start;//.withSecond(0);
				long minutesToAdd = (diffMin/categoryDiff > 0 ? diffMin/categoryDiff : 1);
				System.out.println(minutesToAdd);
				
				stampTimes.add(tempTime.format(formatter));
				while (tempTime.isBefore(stop)) {
					tempTime = tempTime.plusMinutes(minutesToAdd);
					stampTimes.add(tempTime.format(formatter));
					//System.out.println("Temptime: " + tempTime.format(formatter));
				}
			}
			else{
				//Graph by seconds
				formatPattern = "mm:ss";
				formatter = DateTimeFormatter.ofPattern(formatPattern);
				LocalTime tempTime = start;
				long secondstoAdd = (diffSec/categoryDiff > 0 ? diffSec/categoryDiff : 1);
				System.out.println(secondstoAdd);
				
				stampTimes.add(tempTime.format(formatter));
				// TODO: find out why code freezes here.
				while (tempTime.isBefore(stop)) {
					tempTime = tempTime.plusSeconds(secondstoAdd);
					stampTimes.add(tempTime.format(formatter));
					//System.out.println("Temptime: " + tempTime.format(formatter));
				}					
			}
			xAxis.setCategories(stampTimes);
			System.out.println(stampTimes);
			int currentTimeStampIndex = 0;
			int[] counters = new int[stampTimes.size()];
			
			for (int i = 0; i < stampArr.length(); i++) {				
				String stamp = LocalTime.parse(stampArr.getString(i).substring(11)).format(formatter);
				//They are ordered, so checking the previous stamp is enough
				//String newStamp = stamp.format(formatter);
				//System.out.println(stamp); //Debug print
				int hh;
				int mm;
				int ss;
				int catHH;
				int catMM;
				int catSS;
				
				
				System.out.println("SWITCH: "+ formatPattern);
				switch(formatPattern) {
					case "HH":
						//TODO add to the graph by hours
						hh = Integer.parseInt(stamp);
						catHH = Integer.parseInt(stampTimes.get(currentTimeStampIndex));
						//System.out.println("> " + String.valueOf(hh) + " || " + String.valueOf(catHH));
						while(hh > catHH){
							currentTimeStampIndex ++;
							catHH= Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(0, 2));
							catMM = Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(3));
							
							//System.out.println("> " + String.valueOf(hh) + " || " + String.valueOf(catHH));
						}
						
						counters[currentTimeStampIndex] ++;
						break;
					case "hh:mm":
						//TODO add to the graph by minutes
						hh = Integer.parseInt(stamp.substring(0, 2));
						mm = Integer.parseInt(stamp.substring(3));
						catHH = Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(0, 2));
						catMM = Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(3));
						//System.out.println("> " + String.valueOf(hh) + " || " + String.valueOf(catHH));
						//System.out.println("> " + String.valueOf(mm) + " || " + String.valueOf(catMM));	
						while(hh > catHH || mm > catMM){
							currentTimeStampIndex ++;
							catHH= Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(0, 2));
							catMM = Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(3));
							
							//System.out.println("> " + String.valueOf(hh) + " || " + String.valueOf(catHH));
							//System.out.println("> " + String.valueOf(mm) + " || " + String.valueOf(catMM));	
							
						}
						
						counters[currentTimeStampIndex] ++;
						break;
					case "mm:ss":
						//TODO add to the graph by seconds
						mm = Integer.parseInt(stamp.substring(0, 2));
						ss = Integer.parseInt(stamp.substring(3));
						catMM = Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(0, 2));
						catSS = Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(3));
						//System.out.println("> " + String.valueOf(mm) + " || " + String.valueOf(catMM));	
						//System.out.println("> " + String.valueOf(ss) + " || " + String.valueOf(catSS));
						while(mm > catMM ||ss > catSS){

							System.out.println(""); //TODO remove
							currentTimeStampIndex ++;
							catMM = Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(0, 2));
							catSS = Integer.parseInt(stampTimes.get(currentTimeStampIndex).substring(3));
							
							//System.out.println("> " + String.valueOf(mm) + " || " + String.valueOf(catMM));	
							//System.out.println("> " + String.valueOf(ss) + " || " + String.valueOf(catSS));
							
						}
						
						counters[currentTimeStampIndex] ++;
						break;
				}
			}
			XYChart.Series<String, Integer> series = new XYChart.Series<>();
			for (int i = 0; i < counters.length; i++) {
				System.out.println("FILLING DATA TO CHART..."); //TODO remove
				series.getData().add(new XYChart.Data<>(stampTimes.get(i), counters[i]));
			}
			
			//Display info
			LectureNameText.setText(name);
			studPresText.setText(String.valueOf(studentsJoined));
			startTimeText.setText(start.toString());
			stopTimeText.setText(stop.toString());
			Platform.runLater(() -> lostMeBarChart.getData().add(series));			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	// not used in this view.
	@Override
	public void fetchLectures() {
		
	}

	// not used in this view.
	@Override
	public void recieveLectures(JSONObject obj) {
		
	}

}
