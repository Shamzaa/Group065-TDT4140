package program.ui.controllers;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import program.ClientMain;

public class RootController{
	@FXML Button backButton;
	@FXML Text titleText;
	//private AppBinder centerController;
	private ArrayList<String> paths;
	private ClientMain main;
	private boolean skipAddPath;
	private boolean backOnlyLocal = false; //If this is true, the backbutton will only do local functions, and not change window
	
	@FXML
	public void initialize(){
		System.out.println("Root controller initialized");
		paths = new ArrayList<>();
		skipAddPath = false;
		backButton.setDisable(true);
		backButton.setOnAction(e -> goBack());
	}
	
	private void goBack() {
		main.getActiveController().closeController();
		//centerController.closeController();
		System.out.println("Going back");
		if(backOnlyLocal){
			main.getActiveController().localBackChanges();
			return;
		}
		//System.out.println();
		
		String prevPage = paths.get(paths.size()-2);
		System.out.println("Prev page:    " + prevPage);
		
		removeCurrentPage();
		skipAddPath = true;
		main.loadUI(prevPage);
		skipAddPath = false;
	}
	
	private void removeCurrentPage(){
		System.out.println("removing the last path");
		paths.remove(paths.size()-1);
		System.out.println(paths);
		if (paths.size() < 2) {
			backButton.setDisable(true);
		}
	}
	
	
	public void addNewPath(String path){
		if(path.equals("ui/StudentWindow.fxml")){
			return; // prevents the user from experiencing a bug that occurs if you go back and forth between lectures when you return to lecture selector
		}
		System.out.println("adding: " + path);
		paths.add(path);
		//main.getActiveController();
		System.out.println(paths);
		if (paths.size() > 1) {
			backButton.setDisable(false);
		}
		
	}
	public boolean skipAddPath(){
		return skipAddPath;
	}
	
	public void setTitle(String title){
		titleText.setText(title);
	}
	
	public void setMainApp(ClientMain main) {
		this.main = main;
	}
	
	public void setBackOnlyLocal(boolean backOnlyLocal) {
		this.backOnlyLocal = backOnlyLocal;
	}
}
