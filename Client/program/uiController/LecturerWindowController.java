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
	ClientMain main;									//Refernce to the clientMain class that runs the program

	ArrayList<Question> questionList = new ArrayList<>();	//Strings for all questions [Unused? TODO remove]
	private int connectedStudents = 0;					//The number of students currently connected
	private int lostStudents = 0;						//The number of students who are currently lost
	private int liveLectureID;							//The ID used to reference the lecture data table
	
	private ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
	
	@FXML VBox QuestionContainer;		//QuestionBoxes are added to this container, so they appear in the view as a list
	@FXML Arc lostMeRedArc;				//The red part of the "You Lost Me" pie-chart
	@FXML Arc lostMeGreenArc;			//The green part of the "You Lost Me" pie-chart
	@FXML Text lostMeRedText;			//The text that shows the precentage of lost students
	@FXML Text lostMeGreenText;			//The text that shows the percentage of students that are NOT lost
	@FXML Text studentsConnectedText;	//The text that shows how many students are connected
	@FXML Text lectureTitleText; 		//The text that shows the classID from the main class for the lecture, so students know what to connect to
	@FXML Text lectureNameText;			//The text that shows the name the lecturer decided for the live lecture
	
	
	@FXML
	public void initialize(){
		updatePieChartValues();
		updateStudentsConnectedAmount();
	}
	/**@author Anders
	 * This method changes the title text and name text
	 */
	public void setTitleAndNameText(String newTitle, String newName){
		lectureTitleText.setText(newTitle);
		lectureNameText.setText(newName);
	}
	/** @author Anders
	 * This method checks connectedStudents and updates the text
	 */
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

	/** @author Anders
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
	/** @author Anders
	 *  Increments the number of students, and update ui elements
	 */
	public void studentJoined(){
		connectedStudents++;
		updateStudentsConnectedAmount();
		updatePieChartValues();
	}
	/** @author Anders
	 *  Increments the number of LOST students, and updates the pieChart
	 */
	public void studentLost(){
		lostStudents ++;
		updatePieChartValues();
	}
	
	/** @author Anders
	 *  Adds a new question to the VBox container
	 *  @param question A Question object of the question that will be displayed
	 */
	private void addQuestion(Question question){		
		System.out.println("Adding question: " + question);
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
		setTitleAndNameText(main.getClassID(), main.getLectureName());
		
	}
	//-> Functions for QuestionReciever
	@Override
	public void fetchQuestions(int numberOfQuestions){
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
