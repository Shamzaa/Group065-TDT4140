package program.ui.controllers.lecturer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import program.ClientMain;
import program.connection.listeners.ClientListener;
import program.connection.listeners.LectureReciever;
import program.connection.listeners.LectureStatListener;
import program.ui.controllers.AppBinder;

/**
 * Controller for the overview page of a subject. This page lets the user see a list
 * of all previous lectures of a subject, and open them to see stats and other information
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class LectureOverviewController implements AppBinder, LectureReciever{
	
	private ClientMain main;
	
	private ExecutorService clientProcessingPool = Executors.newSingleThreadExecutor();
	
	private Collection<LectureBoxController> lectures = new ArrayList<LectureBoxController>();
	
	@FXML VBox lectureContainer;
	@FXML Text lecturesStat;
	@FXML ProgressIndicator progress;
	@FXML Label classCodeLabel;
	@FXML Label lecturesCount;
	@FXML Label studentsCount;
	@FXML Button createLectureButton;
	
	/**
	 * 
	 */
	@FXML
	private void initialize(){
		createLectureButton.setOnAction(
				e->createNewLecture());
		
	}
	
	/**
	 * Adds a lecture to the list of previous lectures
	 * @param lectureName name of the lecture
	 * @param date date of the lecture
	 * @param lectureID ID of the lecture
	 * @param studentsJoined how many students joined the lecture
	 */
	public void addLecture(String lectureName, String date, int lectureID, int studentsJoined){
		
		
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/fxml/LectureBox.fxml"));
		Platform.runLater(() -> {
			try {
			AnchorPane lPane = (AnchorPane) loader.load();
			// sets listener and attributes
			LectureBoxController controller = loader.getController();
			
			controller.initialize();

			controller.setLectureID(lectureID);
			controller.setLectureName(lectureName);
			//controller.setStudentsCount(studentsJoined);
			controller.setDate(date);
			
			controller.viewButton().setOnAction(e -> viewLecture(controller.getLectureID()));
			
			
			// Adds the questionBox ui element to QuestionContainer
			lectureContainer.getChildren().add(0, lPane);
			
			lectures.add(controller);
			lecturesCount.setText(String.valueOf(Integer.valueOf(lecturesCount.getText()) + 1));
			studentsCount.setText((String.valueOf(Integer.valueOf(studentsCount.getText()) + studentsJoined)));
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
	}
	
	/**
	 * loads the view where you can create a new lecture
	 */
	private void createNewLecture(){
		main.loadUI("ui/fxml/CreateLecture.fxml");
	}
	
	/**
	 * method called from the buttons in the lecture boxes,
	 * lets the program open a new view showing information about that specific lecture
	 * @param lectureID ID of the lecture
	 */
	private void viewLecture(int lectureID){
		
		main.setLectureID(lectureID);

		clientProcessingPool.shutdown();
		main.loadUI("ui/fxml/LectureReview.fxml");

	}
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#setMainApp(program.ClientMain)
	 */
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		LectureStatListener l = new LectureStatListener(main, this, 1);
		fetchLectures();
		classCodeLabel.setText(main.getClassID());
		clientProcessingPool.submit(l);

		main.getRootController().setTitle("Lectures Overview");
	}
	
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#closeController()
	 */
	@Override
	public void closeController() {
		// TODO Make sure all threads and such are closed
	}
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#localBackChanges()
	 */
	@Override
	public void localBackChanges() {
		//UNUSED in this window at the moment		
	}
	//-> From LectureReciever
	/* (non-Javadoc)
	 * @see program.connection.listeners.LectureReciever#recieveLectures(org.json.JSONObject)
	 */
	@Override
	public void recieveLectures(JSONObject obj) {
		try {
			JSONArray objList = obj.getJSONArray("List");
			for (int i = 0; i < objList.length(); i++) {
				JSONObject part = objList.getJSONObject(i);
				String lectureName = part.getString("lectureName");
				int id = part.getInt("id");
				String date = part.getString("date");
				int studentsJoined = part.getInt("studentsJoined");
				
				addLecture(lectureName, date, id, studentsJoined);
			}
			progress.setVisible(false);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see program.connection.listeners.LectureReciever#fetchLectures()
	 */
	@Override
	public void fetchLectures() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "FetchLectures");
			obj.put("ClassID", main.getClassID());

			main.getServerManager().sendJSON(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Unused in this view.
	/* (non-Javadoc)
	 * @see program.connection.listeners.LectureReciever#recieveQuestions(org.json.JSONObject)
	 */
	@Override
	public void recieveQuestions(JSONObject obj) {
		// TODO Auto-generated method stub
		
	}
	// unused in this view.
	/* (non-Javadoc)
	 * @see program.connection.listeners.LectureReciever#recieveLectureReview(org.json.JSONObject)
	 */
	@Override
	public void recieveLectureReview(JSONObject obj) {
		// TODO Auto-generated method stub
		
	}
}
