package program.uiController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
	ArrayList<String> questionList = new ArrayList<>();
	
	private ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
	private int liveLectureID;
	
	
	ClientMain main;
	
	@FXML ProgressIndicator questionLoadIndicator;
	@FXML Button questionButton;
	@FXML Button lostMeButton;
	@FXML Button submitQuestionButton;
	
	@FXML VBox QuestionContainer;
	@FXML GridPane askQuestionContainer;
	@FXML Rectangle transparentOverlay;
	@FXML TextArea askQuestionTextField;
	
	@FXML
	public void initialize() {
		System.out.println("Opening studentwindow");
		questionButton.setOnAction(
				e -> handleQuestionButtonAction());
		lostMeButton.setOnAction(
				e -> handleLostMeButtonAction());
		submitQuestionButton.setOnAction(
				e -> handleSubmitButtonAction());
			
	}
	private void handleQuestionVote(QuestionBoxController controller, String wasOn, String isNowOn){
		System.out.println("{"+controller.getQuestionId()+"} "+controller.getQuestionText() + " changed from [" + wasOn + "] to [" + isNowOn +"]");
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "VoteQuestion");
			obj.put("GoodVote", isNowOn.equals("good"));
			obj.put("QuestionID", controller.getQuestionId());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if((wasOn.equals("good") && isNowOn.equals("bad")) || (wasOn.equals("bad") && isNowOn.equals("good"))){
			//TODO rework a more decent solution: Jumps two scores, sends twice for now
			main.getServerManager().sendJSON(obj);
			main.getServerManager().sendJSON(obj);
		} else {
			main.getServerManager().sendJSON(obj);
		}
	}
	
	private void addQuestion(String question, int questionID){
		System.out.println("Adding question: " + question);
		questionList.add(question);
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/QuestionBox.fxml"));
		System.out.println("Trying to add");
		Platform.runLater(() -> {
			try {
			AnchorPane qPane = (AnchorPane) loader.load();
			/*for (Node node : qPane.getChildren()) {
				if (node.getId().equals("QuestionText")){
					((TextArea) node).setText(question);
					
					break;
				}
			}*/
			// Runs Controller functions
			QuestionBoxController controller = loader.getController();
			controller.goodProperty().addListener((obs, wasOn, isNowOn) -> handleQuestionVote(controller, wasOn, isNowOn));
			controller.setQuestionText(question);
			controller.setQuestionId(questionID);
			// Adds the questionBox ui element to QuestionContainer
			QuestionContainer.getChildren().add(qPane);
			QuestionContainer.getChildren().add(new Separator());
			
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
	}
	

	private void handleSubmitButtonAction() {
		// TODO Auto-generated method stub
		String question = askQuestionTextField.getText();
		askQuestionTextField.setText("");
		askQuestionContainer.setVisible(false);		
		
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
			obj.put("LiveID", liveLectureID);
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
				String question = part.getString("question");
				int id = part.getInt("id");
				// int rating = ...
				// Time time = ...
				
				//System.out.println("Part:     " + part);
				//System.out.println("Question: " + question);
				
				addQuestion(question, id);
			}
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
}
