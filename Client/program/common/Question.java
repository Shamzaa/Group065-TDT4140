package program.common;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.scene.layout.AnchorPane;

/**
 * Question object used as a listener for the question list for both lecturer and student
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 */
public class Question {
	private int 	id;
	private String 	questionText;
	private String 	time;
	private AnchorPane relatedQuestionPane;
	
	private final IntegerProperty rating = new SimpleIntegerProperty();
	
	/**
	 * @param id id of the question used in the database
	 * @param text actual question text
	 * @param time time a question was asked
	 * @param rating current rating of the question
	 */
	public Question(int id, String text, String time, int rating) {
		this.id=id;
		this.questionText=text;
		this.time=time;
		this.rating.set(rating);
	}
	//- Getters --------------------------
	/**
	 * getter to get ID of the question
	 * @return id of the question used in the database
	 */
	public int getId() {
		return id;
	}
	/**
	 * getter to get the question text
	 * @return question text
	 */
	public String getQuestionText() {
		return questionText;
	}
	/**
	 * getter to get the time of the question
	 * @return time of the question
	 */
	public String getTime() {
		return time;
	}
	/**
	 * getter to get the window element of the question list
	 * @return window element of the question
	 */
	public AnchorPane getRelatedQuestionPane() {
		return relatedQuestionPane;
	}
	//- Setters --------------------------
	/**
	 * sets the id of the question
	 * @param id question id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * sets the text of the question
	 * @param questionText question text
	 */
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	/**
	 * sets the time of the question
	 * @param time question time
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * sets the window element related to this question
	 * @param relatedQuestionPane window element
	 */
	public void setRelatedQuestionPane(AnchorPane relatedQuestionPane) {
		this.relatedQuestionPane = relatedQuestionPane;
	}
	//- Metoder for observable property ---------------------------
	/**
	 * sets the rating of the question
	 * @param rating rated score of the question
	 */
	public void setRating(int rating) {
		this.rating.set(rating);		
	}
	/**
	 * getter for the rating of the question
	 * @return rated score of the question
	 */
	public int getRating(){
		return this.rating.get();
	}
	
	/**
	 * getter for the window element to update the rating number, whenever rating 
	 * changes in the Question object.
	 * @return IntegerProperty that can be used as a listener.
	 */
	public IntegerProperty ratingProperty(){
		return this.rating;
	}
	
}
