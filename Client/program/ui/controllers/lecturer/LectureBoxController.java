package program.ui.controllers.lecturer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * this controller is used for each box you see when you get a list of all the lectures
 * in a subject
 * @author Erling Ihlen
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class LectureBoxController {
	
	// attributes
	private int lectureID;
	private String lectureName;
	private int studentsCount;
	
	
	@FXML Button viewLectureButton;
	@FXML Label lectureNameLabel;
	@FXML Label dateLabel;
	
	/**
	 * inits view
	 */
	@FXML
	public void initialize(){
		
	}
	

	
	/**
	 * sets the ID of the lecture this box is related to
	 * @param lectureID ID of lecture
	 */
	public void setLectureID(int lectureID){
		this.lectureID = lectureID;
	}
	
	/**
	 * sets the name of the lecture to be accessible from this box
	 * @param lectureName name of the lecture
	 */
	public void setLectureName(String lectureName){
		this.lectureName = lectureName;
		lectureNameLabel.setText(lectureName);
		
	}
	
	/**
	 * sets the date of this lecture
	 * @param date date of the lecture
	 */
	public void setDate(String date){
		dateLabel.setText(date);
	}
	
	/**
	 * getter to get the id of the lecture
	 * @return id of the lecture
	 */
	public int getLectureID(){
		return lectureID;
	}
	
	/**
	 * Getter to get the button that allows the lecturer to open the lecture.
	 * used as a listener to open a view
	 * @return view lecture button
	 */
	public Button viewButton(){
		return viewLectureButton;
	}

}
