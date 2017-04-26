package program.connection;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import program.ServerMain;

/**
 * This class decodes all incoming JSONObjects and encodes and sends return-JSONs
 * @author Erling Ihlen
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class ClientsManager {
	//Reference objects
	private Collection<ClientConnection> clientsConnected;
	private Map<String, ClientConnection> classIDToConnection;
	private CommandsManager comManager;
	public ServerMain main;
	
	// made so that we can add this clientsManager to parameters inside serverTask
	private ClientsManager clientsManager;
	
	/**
	 * This constructor initializes the reference objects, and starts the thread that listens for new connections
	 * @param portNumber An {@code integer} with the network port that the clients connect through
	 * @param main A {@code ServerMain} object giving a reference to the main server program
	 */
	public ClientsManager(int portNumber, ServerMain main){
		clientsConnected = new ArrayList<ClientConnection>();
		clientsManager = this;
		comManager = new CommandsManager(this);
		this.main = main;
		classIDToConnection = new HashMap<String, ClientConnection>();
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(500);
		
		Runnable serverTask = new Runnable(){
			@Override
			public void run(){
				try{
					ServerSocket serverSocket = new ServerSocket(portNumber);
					System.out.println("Waiting for clients...");
					while(true){
						Socket clientSocket = serverSocket.accept();
						ClientConnection client = new ClientConnection(clientSocket, comManager, clientsManager);
						clientProcessingPool.submit(client);
						clientsConnected.add(client);
					}
				}catch(IOException e){
					System.out.println("couldn't process client request");
					e.printStackTrace();
				}
			}
		};
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}
	
	/**
	 * Gets a complete list of all the clients connected to the socket
	 * @return returns a {@code Collection<ClientConnection>} of all connected clients
	 */
	public Collection<ClientConnection> getClientsConnected(){
		return clientsConnected;
	}
	
	/**
	 * This method adds a lecturer to a given lecture
	 * @param client The connection of the lecturer to be added
	 * @param classID A {@code String} giving the subject code the lecture is happening in
	 */
	public void addLecturerToLecture(ClientConnection client, String classID){
		if(!doesLectureExist(classID)){
			classIDToConnection.put(classID, client);
			client.setClassID(classID);
		}else{
			// TODO: return error message to client requesting classID
		}
	}
	
	/**
	 * This method removes a lecturer from a given lecturer
	 * @param client The connection of the lecturer to be removed
	 */
	public void removeLecturerFromLecture(ClientConnection client){
		if(doesLectureExist(client.getClassID()) && classIDToConnection.get(client.getClassID()) == client){
			classIDToConnection.remove(client.getClassID());
			clientsManager.main.getDatabase().setEndLecture(client.getLectureID());
		}
	}
	
	/**
	 * Removes a given {@code ClientConnection} from the list of connected clients
	 * @param client The connection to be removed
	 */
	public void removeConnection(ClientConnection client){
		clientsConnected.remove(client);
	}
	/**
	 * Adds a given {@code ClientConnection} to the list of connected clients
	 * @param client The connection to be added
	 */
	public void addClientToCollection(ClientConnection client){
		clientsConnected.add(client);
	}
	/**
	 * This method sends a given {@code JSONObject} to the lecturer holding a given lecture
	 * @param obj The {@code JSONObject} that should be passed on to the lecturer
	 * @param classID A {@code String} giving the id of the subject the lecture is happening in
	 */
	public void sendInfoToLecturer(JSONObject obj, String classID){
		if(doesLectureExist(classID)){
			// is case sensitive
			classIDToConnection.get(classID).sendJSON(obj);
		}else{
			throw new IllegalArgumentException("No lecturer holding a lecture in " + classID);
		}
	}
	/**
	 * This method checks if a lecture is being held in a given subject
	 * @param classID A {@code String} giving the subject that should be checked
	 * @return Returns {@code true} if a lecture is being held in the given subject
	 */
	public boolean doesLectureExist(String classID){
		// is case sensitive
		return classIDToConnection.containsKey(classID);
	}
	
	/**
	 * Gets the connection of the lecturer holding a lecture in a given subject
	 * @param classID A {@code String} giving the subject code for that should be checked for lecturers
	 * @return Returns a {@code ClientConnection} to the found lecturer
	 */
	public ClientConnection getLecturer(String classID){
		return classIDToConnection.get(classID);
	}
	
}
