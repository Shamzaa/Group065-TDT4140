package program;
import program.connection.*;
import program.database.Database;

/**
 * The main program for running the server
 * @author Erling Ihlen
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class ServerMain {
	//Management classes
	private ClientsManager clientsManager;
	private Database database;
	
	/**
	 * This method starts a connection with the database, and sets up the clients-manager for the given port
	 * @param portNumber the port-number that the created clientsManager will interact with
	 */
	private void init(int portNumber){
		database = new Database();
		database.connect();
		clientsManager = new ClientsManager(portNumber, this);
	}
	
	/**
	 * Launches the server
	 * @param args
	 */
	public static void main(String[] args){
		ServerMain main = new ServerMain();
		main.init(2222);
	}
	
	/**
	 * Getter for the database
	 * @return returns the Database class the server is connected to
	 */
	public Database getDatabase(){
		return database;
	}
	
	/**
	 * Getter for the servers clients-manager
	 * @return Returns the clientManager class the server is using
	 */
	public ClientsManager getClientsManager(){
		return clientsManager;
	}
}
