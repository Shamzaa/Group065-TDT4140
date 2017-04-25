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
	private ClientsManager clientsManager;
	private Database database;
	
	
	private void init(int portNumber){
		database = new Database();
		database.connect();
		clientsManager = new ClientsManager(portNumber, this);
	}
	
	public static void main(String[] args){
		ServerMain main = new ServerMain();
		main.init(2222);
	}
	
	public Database getDatabase(){
		return database;
	}
	
	public ClientsManager getClientsManager(){
		return clientsManager;
	}
}
