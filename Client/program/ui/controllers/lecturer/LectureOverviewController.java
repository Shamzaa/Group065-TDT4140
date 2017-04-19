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
	
	@FXML
	private void initialize(){
		createLectureButton.setOnAction(
				e->createNewLecture());
		
	}
	
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
	
	private void createNewLecture(){
		main.loadUI("ui/fxml/CreateLecture.fxml");
	}
	
	private void viewLecture(int lectureID){
		
		main.setLectureID(lectureID);

		clientProcessingPool.shutdown();
		main.loadUI("ui/fxml/LectureReview.fxml");

	}
	
	public LectureBoxController getLectureBoxController(int lectureBoxIndex){
		if(main.DEBUG){
			return ((ArrayList<LectureBoxController>) lectures).get(lectureBoxIndex);
		}
		return null;
	}
	//- Functions from interfaces ----------------------------------------------------------------------
	//-> From AppBinder
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		LectureStatListener l = new LectureStatListener(main, this, 1);
		fetchLectures();
		classCodeLabel.setText(main.getClassID());
		clientProcessingPool.submit(l);

		main.getRootController().setTitle("Lectures Overview");
	}
	
	@Override
	public void closeController() {
		// TODO Make sure all threads and such are closed
	}
	@Override
	public void localBackChanges() {
		//UNUSED in this window at the moment		
	}
	//-> From LectureReciever
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
	@Override
	public void recieveQuestions(JSONObject obj) {
		// TODO Auto-generated method stub
		
	}
	// unused in this view.
	@Override
	public void recieveLectureReview(JSONObject obj) {
		// TODO Auto-generated method stub
		
	}
}
