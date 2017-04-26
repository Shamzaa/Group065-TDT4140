package program.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class handles the serverside information of a single connected client
 * @author Erling Ihlen
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class ClientConnection implements Runnable{
	// References to external objects to help with features
	private CommandsManager comManager;
	private ClientsManager clientsManager;
	
	// Connection attributes
	private Socket clientSocket;
	private String role;
	private int lectureID;
	private String classID;
	
	// in and out data channels
	private BufferedReader in;
	private PrintWriter out;
	
	/**
	 * This constructor initializes the reference objects and  and the socket
	 * @param clientSocket The {@code Socket} the client is connected through
	 * @param comManager The {@code CommandsManager} that will handle {@code JSONObject} received from this client
	 * @param clientsManager The {@code ClientsManager} giving references to other connected clients
	 */
	public ClientConnection(Socket clientSocket, CommandsManager comManager, ClientsManager clientsManager){
		this.clientSocket = clientSocket;
		this.comManager = comManager;
		this.clientsManager = clientsManager;
	}
	
	/**
	 * Sets a role to the client
	 * <br> Possible roles (As of 26-04-2017):
	 * <pre>student</pre>
	 * <pre>lecturer</pre>
	 * @param role
	 */
	public void setRole(String role){
		System.out.println("role: '" + role + "' assigned to socket");
		this.role = role;
	}
	
	/**
	 * Sends a given {@code JSONObject to the client}
	 * @param obj The {@code JSONObject} to be sent to the client
	 */
	public void sendJSON(JSONObject obj){
		String json_data = obj.toString();
		out.println(json_data);
	}
	
	/**
	 * Setter for  which lecture this client is in
	 * @param lectureID A {@code integer} with the given id (Should be the same as in the database)
	 */
	public void setLectureID(int lectureID){
		System.out.println("lecture ID: " + lectureID + " set to socket");
		this.lectureID = lectureID;
	}
	
	/**
	 * Getter for the id for a lecture this client is in
	 * @return {@code integer} thats the id for a lecture
	 */
	public int getLectureID(){
		return lectureID;
	}
	
	/**
	 * Setter for which subject this client is looking at
	 * @param classID A {@code String} giving a subject code in the database
	 */
	public void setClassID(String classID){
		this.classID = classID;
	}
	
	/**
	 * Getter for which subject the client is looking at
	 * @return
	 */
	public String getClassID(){
		return classID;
	}
	
	/**
	 * Removes the connection from the clientsManager
	 */
	private void removeConnectionToClient(){
		if(role.equals("Lecturer")){
			clientsManager.removeLecturerFromLecture(this);
			role = "";
		}
		clientsManager.removeConnection(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		System.out.println("New connection!");
		
		try{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		
			while(true){
				System.out.println("Listens for new input");
				try{

					String input = in.readLine();
					JSONObject obj = new JSONObject(input);
					comManager.analyzeFunction(obj, this);
				}catch(JSONException e){
					// data recieved wasn't a json object
					// close connection or ignore.
				}catch(SocketException e){
					// error with connection to client. Remove Client from server and end lecture if it's a lecturer that got lost.
					removeConnectionToClient();
					System.out.println("User disconnected/timed out");
					return;
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
