package classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.scene.layout.AnchorPane;

public class Question {
	private int 	id;
	private String 	questionText;
	private String 	time;
	//private int 	rating;
	private AnchorPane relatedQuestionPane;
	
	private final IntegerProperty rating = new SimpleIntegerProperty();
	
	public Question(int id, String text, String time, int rating) {
		this.id=id;
		this.questionText=text;
		this.time=time;
		this.rating.set(0);
	}
	//- Getters --------------------------
	public int getId() {
		return id;
	}
	public String getQuestionText() {
		return questionText;
	}
	public String getTime() {
		return time;
	}
	public AnchorPane getRelatedQuestionPane() {
		return relatedQuestionPane;
	}
	//- Setters --------------------------
	public void setId(int id) {
		this.id = id;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setRelatedQuestionPane(AnchorPane relatedQuestionPane) {
		this.relatedQuestionPane = relatedQuestionPane;
	}
	//- Metoder for observable property ---------------------------
	public void setRating(int rating) {
		this.rating.set(rating);		
	}
	public int getRating(){
		return this.rating.get();
	}
	public IntegerProperty ratingProperty(){
		return this.rating;
	}
	
}
