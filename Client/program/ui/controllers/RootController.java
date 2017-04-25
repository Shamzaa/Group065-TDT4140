package program.ui.controllers;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import program.ClientMain;

/**
 * 
 * The controller for the root view of the app, has a back button in the top,
 * and a window in the middle where we load views based on what state the user is in
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class RootController implements AppBinder{
	@FXML Button backButton;
	@FXML Text titleText;
	//private AppBinder centerController;
	private ArrayList<String> paths;
	private ClientMain main;
	private boolean skipAddPath;
	private boolean backOnlyLocal = false; //If this is true, the backbutton will only do local functions, and not change window
	
	/**
	 * inits view
	 */
	@FXML
	public void initialize(){
		System.out.println("Root controller initialized");
		paths = new ArrayList<>();
		skipAddPath = false;
		backButton.setDisable(true);
		backButton.setOnAction(e -> goBack());
	}
	
	/**
	 * Method that makes the user go back to the previous view
	 */
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
	
	/**
	 * removes the view the user went back from.
	 */
	private void removeCurrentPage(){
		System.out.println("removing the last path");
		paths.remove(paths.size()-1);
		System.out.println(paths);
		if (paths.size() < 2) {
			backButton.setDisable(true);
		}
	}
	
	
	/**
	 * Adds the view the user was on, when entering a new view, to the stack, so the back button has something to refer to.
	 * @param path fxml path of previous view.
	 */
	public void addNewPath(String path){
		if(path.equals("ui/fxml/StudentWindow.fxml")){
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
	/**
	 * Used if you don't want to skip a view to add to the stack of backs,
	 * used on our initial loadui when starting the app. Also used to let the stack
	 * know not to add the view you're on, when you go back, so you're not ping
	 * ponging between views.
	 * @return bool to skip or not skip a stack addition
	 */
	public boolean skipAddPath(){
		return skipAddPath;
	}
	
	/**
	 * sets the title in the top of the root controller, used to give the user info
	 * about where he or she is.
	 * @param title the title of the page the user is in.
	 */
	public void setTitle(String title){
		titleText.setText(title);
	}
	/* (non-Javadoc)
	 * @see program.ui.controllers.AppBinder#setMainApp(program.ClientMain)
	 */
	@Override
	public void setMainApp(ClientMain main) {
		this.main = main;
	}
	
	/**
	 * Used so you can press the back button when the question box appear, without exiting the student window
	 * @param backOnlyLocal if you have a popup that you want the back button to close.
	 */
	public void setBackOnlyLocal(boolean backOnlyLocal) {
		this.backOnlyLocal = backOnlyLocal;
	}
}
