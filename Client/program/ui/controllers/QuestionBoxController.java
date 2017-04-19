package program.ui.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import program.common.Question;

public class QuestionBoxController {
	//Class values
	private final StringProperty vote = new SimpleStringProperty("----");	//Used to know what the current chosen radioButton
	private Question question;												//Used to refer to the question in the dataBase
	
	//UI element
	@FXML RadioButton 	goodRadio;			//Used to vote that a question is good
	@FXML RadioButton 	badRadio;			//Used to vote that a question is bad
	//@FXML TextArea 		questionTextArea;	//Textarea to show the question text
	@FXML Pane			pointPane;			//Pane that overlays votebuttons to show score
	@FXML Text			pointScoreText;		//Text that shows how many points a question has
	@FXML Label			questionLabel;
	//@FXML Rectangle		questionTextBG;
	
	private void fitToText(){		
		
		/*//TODO add later. Automatisk justere høyden til boksen for å passe tekstlengden
		Label label = questionLabel;
		String text = question.getQuestionText();
		Double fontSize = label.getFont().getSize();
        String clippedText = Utils.computeClippedText( label.getFont(), label.getText(), label.getWidth(), label.getTextOverrun(), label.getEllipsisString() );
        Font newFont = label.getFont();

        while ( !label.getText().equals( clippedText ) && fontSize > 0.5 )
        {
        	System.out.println( "fontSize = " + fontSize + ", clippedText = " + clippedText );
            fontSize = fontSize - 0.05;
            newFont = Font.font( label.getFont().getFamily(), fontSize );
            clippedText = Utils.computeClippedText( newFont, label.getText(), label.getWidth(), label.getTextOverrun(), label.getEllipsisString() );
        }
		label.setFont( newFont );*/
	}
	
	@FXML
	public void initialize(){
		System.out.println("QuestionBox added");
		goodRadio.setOnAction(
				e -> voteQuestion("good"));
		badRadio.setOnAction(
				e -> voteQuestion("bad"));
		pointScoreText.setText(String.valueOf(0));
	}
	
	public String getQuestionText(){
		return questionLabel.getText();
	}
	
	public void setScoreVisible(boolean value){
		pointPane.setVisible(value);
	}
	public void setQuestion(Question question) {
		this.question = question;
		questionLabel.setText(question.getQuestionText());
		pointScoreText.setText(String.valueOf(question.ratingProperty().get()));
		this.question.ratingProperty().addListener((obs, wasOn, isNowOn) -> pointScoreText.setText(String.valueOf(isNowOn)));
		fitToText();
	}
	public int getQuestionId() {
		return question.getId();
	}	
	//- Methods for StringProperty ----------------------------------
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
	private void viewQuestionScore(int score){
		
	}
}
