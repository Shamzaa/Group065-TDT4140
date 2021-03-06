package program.ui.controllers.lecturer;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import program.common.Question;
import program.connection.listeners.ClientListener;
import program.connection.listeners.QuestionReciever;
import program.ui.controllers.AppBinder;
import program.ui.controllers.QuestionBoxController;

/**
 * The controller for the view the lecturer sees when a lecture is happening
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class LecturerWindowController implements AppBinder, QuestionReciever {
	ClientMain main;										//Reference to the clientMain class that runs the program

	ArrayList<Question> questionList = new ArrayList<>();	//Strings for all questions
	private int connectedStudents = 0;						//The number of students currently connected
	private int lostStudents = 0;							//The number of students who are currently lost
	private int liveLectureID;								//The ID used to reference the lecture data table
	
	//Variables for processing how many students are lost in a window
	private double lostThreshold = 	0.9;	//Decides how many students must be lost for the program to give the lecturer a notification
	//private ArrayList<Timestamp> lostTimes = new ArrayList<>(); //TODO Remove?
	
	// call CL.stopListening() to stop the listener.
	ClientListener CL;
	private ExecutorService clientProcessingPool = Executors.newSingleThreadExecutor();
	
	@FXML VBox QuestionContainer;		//QuestionBoxes are added to this container, so they appear in the view as a list
	@FXML Arc lostMeRedArc;				//The red part of the "You Lost Me" pie-chart
	@FXML Arc lostMeGreenArc;			//The green part of the "You Lost Me" pie-chart
	@FXML Text lostMeRedText;			//The text that shows the percentage of lost students
	@FXML Text lostMeGreenText;			//The text that shows the percentage of students that are NOT lost
	@FXML Text studentsConnectedText;	//The text that shows how many students are connected
	@FXML Text lectureTitleText; 		//The text that shows the classID from the main class for the lecture, so students know what to connect to
	@FXML Text lectureNameText;			//The text that shows the name the lecturer decided for the live lecture
	
	
	/**
	 * inits the view
	 */
	@FXML
	public void initialize(){
		updatePieChartValues();
		updateStudentsConnectedAmount();
		//lectureStartTime = new Timestamp(System.currentTimeMillis());	
	}
	
	/**
	 * This method changes the title text and lecture name text
	 * @param newTitle
	 * @param newName
	 */
	public void setTitleAndNameText(String newTitle, String newName){
		lectureTitleText.setText(newTitle);
		lectureNameText.setText(newName);
	}
	
	/**
	 * This method checks connectedStudents and updates the text
	 */
	public void updateStudentsConnectedAmount(){
		studentsConnectedText.setText(String.valueOf(connectedStudents) + " Students connected");
	}
	
	/**
	 * sorts the question box based on the score of the question.
	 */
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

	/**
	 * This method checks how many students are lost, and updates the pie chart accordingly
	 */
	public void updatePieChartValues(){
		double percentRed = 0;		
		if(connectedStudents == 0){
			percentRed = 0;
		} else {
			percentRed =100*lostStudents/connectedStudents;	
		}
		lostMeRedText.setText(String.valueOf(percentRed)+"%");
		lostMeGreenText.setText(String.valueOf(100 - percentRed)+"%");
		
		double newAngle = 360*(percentRed/100);
		
		lostMeRedArc.setStartAngle(90-newAngle);
		lostMeRedArc.setLength(newAngle);
		lostMeGreenArc.setStartAngle(90);
		lostMeGreenArc.setLength(360-newAngle);
	}

	/**
	 *  Increments the number of students, and update ui elements
	 */
	public void studentJoined(){
		connectedStudents++;
		updateStudentsConnectedAmount();
		updatePieChartValues();
	}
	/**
	 *  Increments the number of LOST students, and updates the pieChart. Also checks and warns the teacher if the threshold is reached
	 *
	 */
	public void studentLost(){
		lostStudents ++;
		updatePieChartValues();
		Timestamp recvTime = new Timestamp(System.currentTimeMillis());
		//lostTimes.add(recvTime);
		System.out.println("Recieved lostMe message at: " + recvTime);
		//System.out.println(lostTimes);
		
		if(((double) lostStudents/(double) connectedStudents) > lostThreshold){
			main.displayAlert("Students lost!", null, "A threshold has been reached, your students are lost! Consider redoing the most recent part of the lecture!");
		}
		
		Timer cooldownTimer = new Timer();
		cooldownTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Discarding studentLost");
				lostStudents --;
				updatePieChartValues();
			}
		}, main.getLostMeTimerLenght()*1000);
	}

	/**
	 *  Adds a new question to the VBox container
	 * @param question A Question object of the question that will be displayed
	 */
	private void addQuestion(Question question){		
		System.out.println("Adding question: " + question);
		questionList.add(question);
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/fxml/QuestionBox.fxml"));
		Platform.runLater(() -> {
			try {
			AnchorPane qPane = (AnchorPane) loader.load();
			
			question.setRelatedQuestionPane(qPane);
			// Runs Controller functions
			QuestionBoxController controller = loader.getController();
			
			controller.setScoreVisible(true);
			controller.setQuestion(question);
			// Adds the questionBox ui element to QuestionContainer
			QuestionContainer.getChildren().add(qPane);
			
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
	}
	
	/**
	 * tells the server the lecture is over, and sets the end timestamp in the database
	 */
	private void endLecture(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "EndLecture");
			main.getServerManager().sendJSON(obj);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#setMainApp(program.ClientMain)
	 */
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		CL = new ClientListener(main, this);
		clientProcessingPool.submit(CL);
		fetchLiveLectureID();
		setTitleAndNameText(main.getClassID(), main.getLectureName());
		main.getRootController().setTitle("Lecture");
	}
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#closeController()
	 */
	@Override
	public void closeController() {
		CL.stopListening();
		endLecture();
	}
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#localBackChanges()
	 */
	@Override
	public void localBackChanges() {
		//UNUSED in this window at the moment		
	}
	//-> Functions for QuestionReciever
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#fetchQuestions(int)
	 */
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
	
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#recieveQuestions(org.json.JSONObject)
	 */
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
	
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#fetchLiveLectureID()
	 */
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
	
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#setLiveLectureID(int)
	 */
	@Override
	public void setLiveLectureID(int ID) {
		System.out.println("Setting liveLectureID to: " + ID);
		this.liveLectureID = ID;
	}
	
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#updateQuestionScore(int, int)
	 */
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
