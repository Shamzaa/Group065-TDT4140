package program.uiController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LectureBoxController {
	
	// attributes
	private int lectureID;
	private String lectureName;
	private int studentsCount;
	
	
	@FXML Button viewLectureButton;
	@FXML Label lectureNameLabel;
	@FXML Label dateLabel;
	
	@FXML
	public void initialize(){
		
	}
	

	
	public void setLectureID(int lectureID){
		this.lectureID = lectureID;
	}
	
	public void setLectureName(String lectureName){
		this.lectureName = lectureName;
		lectureNameLabel.setText(lectureName);
		
	}
	
	/*public void setStudentsCount(int studentsCount){
		this.studentsCount = studentsCount;
		studentsJoinedLabel.setText(Integer.toString(studentsCount));
	}*/
	
	public void setDate(String date){
		dateLabel.setText(date);
	}
	
	public int getLectureID(){
		return lectureID;
	}
	
	public Button viewButton(){
		return viewLectureButton;
	}

}
