package program.uiController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import classes.Question;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import program.ClientMain;
import program.connection.ClientListener;
import program.connection.QuestionReciever;

public class StudentWindowController implements AppBinder, QuestionReciever {
	ArrayList<Question> questionList = new ArrayList<>();
	
	private ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
	private int liveLectureID;
	private int charLimit = 140;
	
	ClientMain main;
	
	@FXML ProgressIndicator questionLoadIndicator;	//Indicator that the program is working on loading the questions
	@FXML Button questionButton;					//Used to open the overlay where a students writes his question
	@FXML Button lostMeButton;						//Used to signal that a student is lost
	@FXML Button submitQuestionButton;				//Used to send a question the student wrote
	
	@FXML VBox QuestionContainer;					//Container for all the questionBoxes
	@FXML GridPane askQuestionContainer;			//Contains the text areas to ask and submit questions
	@FXML Rectangle transparentOverlay;				//
	@FXML TextArea askQuestionTextField;
	@FXML Label remainingCharsLabel;
	
	@FXML
	public void initialize() {
		System.out.println("Opening studentwindow");
		questionButton.setOnAction(
				e -> handleQuestionButtonAction());
		lostMeButton.setOnAction(
				e -> handleLostMeButtonAction());
		submitQuestionButton.setOnAction(
				e -> handleSubmitButtonAction());
		askQuestionTextField.textProperty().addListener(
				(e, oldStr, newStr) -> handleAskQuestionTextChange(oldStr, newStr));
			
	}
	private void handleAskQuestionTextChange(String oldText, String newText){
		System.out.println("[" + String.valueOf(oldText.length()) + "]" + "old: " + oldText);
		System.out.println("[" + String.valueOf(newText.length()) + "]" + "new: " + newText);
		int newLenght = newText.length();
		int remaining = charLimit - newLenght;
		remainingCharsLabel.setText(String.valueOf(remaining));
		if(remaining < 0 && !submitQuestionButton.isDisabled()){
			submitQuestionButton.setDisable(true);
		} 
		else if(remaining >= 0 && submitQuestionButton.isDisabled()){
			submitQuestionButton.setDisable(false);
		}
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
	
	private void handleQuestionVote(QuestionBoxController controller, String wasOn, String isNowOn){
		System.out.println("{"+controller.getQuestionId()+"} "+controller.getQuestionText() + " changed from [" + wasOn + "] to [" + isNowOn +"]");
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "VoteQuestion");
			obj.put("QuestionID", controller.getQuestionId());
			//If the user selects a new vote after already choosing one, its neccessary to move the rating in the database by two increments
			int swap = (wasOn.equals("good") && isNowOn.equals("bad")) || (wasOn.equals("bad") && isNowOn.equals("good"))? 2 : 1;
			obj.put("ScoreChange", (isNowOn.equals("good")? 1*swap : -1*swap ));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		main.getServerManager().sendJSON(obj);
	}
	
	private void addQuestion(Question question){
		questionList.add(question);
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/QuestionBox.fxml"));
		Platform.runLater(() -> {
			try {
			AnchorPane qPane = (AnchorPane) loader.load();
			question.setRelatedQuestionPane(qPane);
			// Runs Controller functions
			QuestionBoxController controller = loader.getController();
			controller.goodProperty().addListener((obs, wasOn, isNowOn) -> handleQuestionVote(controller, wasOn, isNowOn));
			controller.setQuestion(question);
			//controller.setQuestionText(question.getQuestionText());
			// Adds the questionBox ui element to QuestionContainer
			QuestionContainer.getChildren().add(qPane);
			
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
	}
	

	private void handleSubmitButtonAction() {
		// TODO Auto-generated method stub
		String question = askQuestionTextField.getText();

		System.out.println(askQuestionTextField.getText());
		if (!question.equals("")) {
			System.out.println("Submitting question:" + question);
			String classID = main.getClassID();
			JSONObject obj = new JSONObject();
				
			try {
				obj.put("Function", "NewQuestion");
				obj.put("ClassID", classID);
				obj.put("Question", question);
				main.getServerManager().sendJSON(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		askQuestionTextField.setText("");
		askQuestionContainer.setVisible(false);
	}

	private void handleLostMeButtonAction() {
		String classID = main.getClassID();
		lostMeButton.setDisable(true);
		lostMeButton.setText("Sendt!");
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "LostMe");
			obj.put("ClassID", classID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		main.getServerManager().sendJSON(obj);
		
		// TODO: some visual feedback for the student to let him know that he sendt the notification
		//triggers the run() block after lostMeButtonCooldown*1000 milliseconds
		Timer cooldownTimer = new Timer();
		cooldownTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				lostMeButton.setDisable(false);
				Platform.runLater(() -> lostMeButton.setText("YOU LOST ME!"));
				System.out.println("lostButton enabled again!");
			}
		}, main.getLostMeTimerLenght()*1000);
	}

	private void handleQuestionButtonAction() {
		// TODO Auto-generated method stub
		System.out.println("Asking question");
		askQuestionContainer.setVisible(true);
	}

	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
		//fetches all lecture question to fill the list
		clientProcessingPool.submit(new ClientListener(main, this));
		fetchLiveLectureID();

		main.getRootController().setTitle("Lecture");
	}
	@Override
	public void closeController() {
		// TODO Make sure all threads and such are closed
	}
	
	//-> From QuestionReciever
	@Override
	public void fetchQuestions(int numberOfQuestions){
		System.out.println("Fetching questions.....");
		questionLoadIndicator.setVisible(true);
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
		System.out.println("Recieving questions...");
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
			sortQuestionsByScore();
			questionLoadIndicator.setVisible(false);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void fetchLiveLectureID() {
		System.out.println("Fetching LiveLecture.....");
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
		//Now that the live ID is recieved, we can fetch all the questions the lecture got before this client joined
		fetchQuestions(Integer.MAX_VALUE);
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
