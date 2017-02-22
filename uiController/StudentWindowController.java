package program.uiController;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import program.ClientMain;

public class StudentWindowController implements AppBinder {
	ArrayList<String> questionList = new ArrayList<>();
	
	ClientMain main;
	
	
	@FXML Button questionButton;
	@FXML Button lostMeButton;
	@FXML Button submitQuestionButton;
	
	@FXML VBox QuestionContainer;
	@FXML GridPane askQuestionContainer;
	@FXML Rectangle transparentOverlay;
	@FXML TextArea askQuestionTextField;
	
	@FXML
	public void initialize() {
		System.out.println("Opening studentwindow");
		questionButton.setOnAction(
				e -> handleQuestionButtonAction());
		lostMeButton.setOnAction(
				e -> handleLostMeButtonAction());
		submitQuestionButton.setOnAction(
				e -> handleSubmitButtonAction());
		
		addQuestion("monotonectally administrate leveraged initiatives");
		addQuestion("interactively envisioneer reliable e-markets conveniently plagiarize reliable synergy");
		addQuestion("continually reconceptualize one-to-one niches conveniently reinvent maintainable testing procedures uniquely repurpose&#10;customer directed virtualization");
		
	}
	public void addQuestion(String question){
		questionList.add(question);
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ui/QuestionBox.fxml"));
		try {
			
			AnchorPane qPane = (AnchorPane) loader.load();
			for (Node node : qPane.getChildren()) {
				if (node.getId().equals("QuestionText")){
					((TextArea) node).setText(question);
					
					/* TODO add later. Automatisk justere høyden til boksen for å passe tekstlengden
					Text helper = new Text();
					helper.setText(text);
				    helper.setFont(font);
				    helper.setWrappingWidth((int)wrappingWidth);
				    helper.getLayoutBounds().getHeight();
					*/
					break;
				}
			}
			QuestionContainer.getChildren().add(qPane);
			QuestionContainer.getChildren().add(new Separator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private void handleSubmitButtonAction() {
		// TODO Auto-generated method stub
		String question = askQuestionTextField.getText();
		if (!question.equals("")) {
			System.out.println("Submitting question:" + question);
			askQuestionTextField.setText("");
			askQuestionContainer.setVisible(false);
			addQuestion(question);
		}
	}

	private void handleLostMeButtonAction() {
		// TODO Auto-generated method stub
		System.out.println();
	}

	private void handleQuestionButtonAction() {
		// TODO Auto-generated method stub
		System.out.println("Asking question");
		askQuestionContainer.setVisible(true);
	}

	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
		
	}
}
