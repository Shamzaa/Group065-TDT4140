package program.ui.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import program.common.Question;

/**
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 */
public class QuestionBoxController {
	//Class values
	private final StringProperty vote = new SimpleStringProperty("----");	//Used to know what the current chosen radioButton
	private Question question;												//Used to refer to the question in the dataBase
	
	//UI element
	@FXML RadioButton 	goodRadio;			//Used to vote that a question is good
	@FXML RadioButton 	badRadio;			//Used to vote that a question is bad
	@FXML Pane			pointPane;			//Pane that overlays votebuttons to show score
	@FXML Text			pointScoreText;		//Text that shows how many points a question has
	@FXML Label			questionLabel;

	/**
	 * inits the view.
	 */
	@FXML
	public void initialize(){
		System.out.println("QuestionBox added");
		goodRadio.setOnAction(
				e -> voteQuestion("good"));
		badRadio.setOnAction(
				e -> voteQuestion("bad"));
		pointScoreText.setText(String.valueOf(0));
	}
	
	/**
	 * getter to get the text of the question object.
	 * @return question text
	 */
	public String getQuestionText(){
		return questionLabel.getText();
	}
	
	/**
	 * Method used to determine wether the score of the question should be visible
	 * @param bool to determine if we should show score or not
	 */
	public void setScoreVisible(boolean value){
		pointPane.setVisible(value);
	}
	/**
	 * Sets the question object for this window.
	 * @param question question object the window should be related to.
	 */
	public void setQuestion(Question question) {
		this.question = question;
		questionLabel.setText(question.getQuestionText());
		pointScoreText.setText(String.valueOf(question.ratingProperty().get()));
		this.question.ratingProperty().addListener((obs, wasOn, isNowOn) -> pointScoreText.setText(String.valueOf(isNowOn)));

	}
	/**
	 * getter to get the id of the question
	 * @return ID of the question used in the database
	 */
	public int getQuestionId() {
		return question.getId();
	}	
	//- Methods for StringProperty ----------------------------------
	/**
	 * Method used to vote on a the question in this window
	 * @param vote string to determine if a question has been rated good or bad
	 */
	public void voteQuestion(String vote){
		System.out.println("voting");
		this.vote.set(vote);
	}
	/**
	 * getter to get the vote state of the question
	 * @return vote state, good or bad
	 */
	public String getVote(){
		return vote.get();
	}
	/**
	 * get the property of the string of vote.
	 * @return stringproperty to work as a listener
	 */
	public StringProperty goodProperty(){
		return vote;
	}
	public int getQuestionScore(){
		return Integer.valueOf(pointScoreText.getText());
	}
}
