package program.uiController;

import program.ClientMain;

public interface AppBinder {
	/** @author Anders
	 * Sets up necessary references to client main and gets start info from the database, if any
	 * @param main The ClientMain class that runs the application
	 */
	public void setMainApp(ClientMain main);
	
	/** @author Anders
	 * Runs all necessary functions to close threads, if any
	 * Also notifies the server, if necessary(For example if a live lecture ends)
	 */
	public void closeController();
}
