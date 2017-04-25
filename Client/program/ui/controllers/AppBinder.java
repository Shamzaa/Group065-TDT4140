package program.ui.controllers;

import program.ClientMain;

/**
 * interface the controllers can implement, so we can have 1 method in MainClass
 * to load UI, since the controllers are different classes.
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 */
public interface AppBinder {
	/** 
	 * Sets up necessary references to client main and gets start info from the database, if any
	 * @param main The ClientMain class that runs the application
	 */
	public void setMainApp(ClientMain main);
	
	/** 
	 * Runs all necessary functions to close threads, if any
	 * Also notifies the server, if necessary(For example if a live lecture ends)
	 */
	public void closeController();
	
	/** 
	 * Used when the rootController doesn't go back, but the center controller wants to change something locally using the backButton
	 */
	public void localBackChanges();
	
}
