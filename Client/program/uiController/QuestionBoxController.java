package program.uiController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class QuestionBoxController {
	//Class values
	private final StringProperty vote = new SimpleStringProperty("----");
	private int questionId;
	
	//UI element
	@FXML RadioButton 	goodRadio;
	@FXML RadioButton 	badRadio;
	@FXML TextArea 		questionTextArea;
	@FXML Pane			pointPane;
	@FXML Text			pointScoreText;
	
	private void fitToText(){
		//TODO add code

		/* TODO add later. Automatisk justere høyden til boksen for å passe tekstlengden
		Text helper = new Text();
		helper.setText(text);
	    helper.setFont(font);
	    helper.setWrappingWidth((int)wrappingWidth);
	    helper.getLayoutBounds().getHeight();
		*/
	}
	
	@FXML
	public void initialize(){
		System.out.println("QuestionBox added");
		goodRadio.setOnAction(
				e -> voteQuestion("good"));
		badRadio.setOnAction(
				e -> voteQuestion("bad"));
	}
	public String getQuestionText(){
		return questionTextArea.getText();
	}
	
	public void setQuestionText(String question){
		questionTextArea.setText(question);
		//fitToText(); TODO: Implement later
	}
	public void setScore(int score){
		pointScoreText.setText(String.valueOf(score));
	}
	public void setScoreVisible(boolean value){
		pointPane.setVisible(value);
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	public int getQuestionId() {
		return questionId;
	}	
	//- Methods for BooleanProperty ----------------------------------
	public void voteQuestion(String vote){
		System.out.println("voting");
		this.vote.set(vote);
	}
	public String getVote(){
		return vote.get();
	}
	public StringProperty goodProperty(){
		return vote;
	}
}
