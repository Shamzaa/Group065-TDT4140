package program.ui.controllers.student;

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
import program.common.Question;
import program.connection.listeners.ClientListener;
import program.connection.listeners.QuestionReciever;
import program.ui.controllers.AppBinder;
import program.ui.controllers.QuestionBoxController;

/**
 * Controller for the student view when attending a lecture
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class StudentWindowController implements AppBinder, QuestionReciever {
	private ArrayList<Question> questionList = new ArrayList<>();
	private ArrayList<QuestionBoxController> questionBoxControllers;
	
	private ExecutorService clientProcessingPool = Executors.newSingleThreadExecutor();
	private int liveLectureID;
	private int charLimit = 140;
	
	// reference to listener object, so we can call stopListening();
	private ClientListener CL;
	
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
	
	/**
	 * inits view
	 */
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
		
		
		askQuestionContainer.setVisible(false);
		submitQuestionButton.setDisable(true);
			
	}
	
	/**
	 * Sorts the question box based on question score
	 */
	private void sortQuestionsByScore(){
		Platform.runLater(() -> {
			//The -1 reverses the sorting order
			questionList.sort((q1, q2)-> -1*Integer.compare(q1.getRating(), q2.getRating()));
			ObservableList<AnchorPane> workingCollection = FXCollections.observableArrayList();
			for (Question question : questionList) {
				System.out.println(question.getQuestionText());
				System.out.println("changing orders");
				workingCollection.add(question.getRelatedQuestionPane());
			}
			QuestionContainer.getChildren().setAll(workingCollection);
			System.out.println("aylmao");
		});	
	}
	
	
	/**
	 * adds a question to the question box
	 * @param question Question added
	 */
	private void addQuestion(Question question){
		questionList.add(question);
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/fxml/QuestionBox.fxml"));
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
			
			//This is used for debug and unit testing only
			if(main.DEBUG){
				questionBoxControllers.add(controller);
			}
			
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
	}
	
	/**
	 * Method to limit the user from typing too many characters in the ask question box
	 * @param oldText question text before button press
	 * @param newText question text after button press
	 */
	private void handleAskQuestionTextChange(String oldText, String newText){
		int newLenght = newText.length();
		int remaining = charLimit - newLenght;
		remainingCharsLabel.setText(String.valueOf(remaining));
		if((remaining < 0 || newLenght == 0) && !submitQuestionButton.isDisabled()){
			submitQuestionButton.setDisable(true);
		} 
		else if(remaining >= 0 && submitQuestionButton.isDisabled()){
			submitQuestionButton.setDisable(false);
		}
	}
	
	/**
	 * Method called whenever the user votes on a question
	 * @param controller the controller of the questionbox that is voted on
	 * @param wasOn handler to handle duplicate votes
	 * @param isNowOn handler to handle duplicate votes
	 */
	private void handleQuestionVote(QuestionBoxController controller, String wasOn, String isNowOn){
		System.out.println("{"+controller.getQuestionId()+"} "+controller.getQuestionText() + " changed from [" + wasOn + "] to [" + isNowOn +"]");
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "VoteQuestion");
			obj.put("QuestionID", controller.getQuestionId());
			//If the user selects a new vote after already choosing one, its neccessary to move the rating in the database by two increments
			//System.out.println("wasOn: " + wasOn + " | isNowOn" + );
			int swap = (wasOn.equals("good") && isNowOn.equals("bad")) || (wasOn.equals("bad") && isNowOn.equals("good"))? 2 : 1;
			
			
			obj.put("ScoreChange", (isNowOn.equals("good")? 1*swap : -1*swap ));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		main.getServerManager().sendJSON(obj);
	}
	
	/**
	 * method called when a student is submitting a question, sends it to server,
	 * and server distributes it to all clients connected to the lecture.
	 */
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
		main.getRootController().setBackOnlyLocal(false);
	}

	/**
	 * Method called whenever the student presses the "I am lost" button
	 */
	private void handleLostMeButtonAction() {
		String classID = main.getClassID();
		lostMeButton.setDisable(true);
		lostMeButton.setText("Notification sent!");
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "LostMe");
			obj.put("ClassID", classID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		main.getServerManager().sendJSON(obj);
		
		//triggers the run() block after lostMeButtonCooldown*1000 milliseconds
		Timer cooldownTimer = new Timer();
		cooldownTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				lostMeButton.setDisable(false);
				Platform.runLater(() -> lostMeButton.setText("I AM LOST!"));
				System.out.println("lostButton enabled again!");
			}
		}, main.getLostMeTimerLenght()*1000);
	}

	/**
	 * makes ask question view visible to the student
	 */
	private void handleQuestionButtonAction() {
		System.out.println("Asking question");
		askQuestionContainer.setVisible(true);
		main.getRootController().setBackOnlyLocal(true);
	}
	
	/**
	 * Used for unitTesting and debugging ONLY
	 * This method requires debug to be true, else it will always return null
	 * @param questionIndex The index of the question in the vBox container
	 * @return If main.DEBUG is true, this will return the controller for a specified question, else null
	 * @author Anders
	 */
	public QuestionBoxController getQuestionBoxController(int questionIndex) {
		if(main.DEBUG && questionIndex < questionBoxControllers.size()){
			return questionBoxControllers.get(questionIndex);			
		}
		return null;
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
		//fetches all lecture question to fill the list
		fetchLiveLectureID();

		main.getRootController().setTitle("Lecture");
		
		//This is used for unittesting purposes only purposes;
		if(main.DEBUG){
			 questionBoxControllers = new ArrayList<>();
		}
	}
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#closeController()
	 */
	@Override
	public void closeController() {
		CL.stopListening();
	}
	
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#localBackChanges()
	 */
	@Override
	public void localBackChanges() {
		askQuestionTextField.setText("");
		askQuestionContainer.setVisible(false);
		submitQuestionButton.setDisable(true);
		main.getRootController().setBackOnlyLocal(false);
	}
	
	//-> From QuestionReciever
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#fetchQuestions(int)
	 */
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
	
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#recieveQuestions(org.json.JSONObject)
	 */
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
	
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#fetchLiveLectureID()
	 */
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
	
	/* (non-Javadoc)
	 * @see program.connection.listeners.QuestionReciever#setLiveLectureID(int)
	 */
	@Override
	public void setLiveLectureID(int ID) {
		System.out.println("Setting liveLectureID to: " + ID);
		this.liveLectureID = ID;
		//Now that the live ID is recieved, we can fetch all the questions the lecture got before this client joined
		fetchQuestions(Integer.MAX_VALUE);
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
