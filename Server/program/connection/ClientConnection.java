package program.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientConnection implements Runnable{
	// references to external objects to help with features
	private CommandsManager comManager;
	private ClientsManager clientsManager;
	
	// connection atributes
	private Socket clientSocket;
	private String role;
	private int lectureID;
	private String classID;
	// in and out data channels
	private BufferedReader in;
	private PrintWriter out;
	
	public ClientConnection(Socket clientSocket, CommandsManager comManager, ClientsManager clientsManager){
		this.clientSocket = clientSocket;
		this.comManager = comManager;
		this.clientsManager = clientsManager;
	}
	
	public void setRole(String role){
		System.out.println("role: '" + role + "' assigned to socket");
		this.role = role;
	}
	
	public void setLectureID(int lectureID){
		System.out.println("lecture ID: " + lectureID + " set to socket");
		this.lectureID = lectureID;
	}
	
	public void sendJSON(JSONObject obj){
		String json_data = obj.toString();
		out.println(json_data);
	}
	
	
	public int getLectureID(){
		return lectureID;
	}
	
	public void setClassID(String classID){
		this.classID = classID;
	}
	
	public String getClassID(){
		return classID;
	}
	
	private void removeConnectionToClient(){
		if(role.equals("Lecturer")){
			clientsManager.removeLecturerFromLecture(this);
			role = "";
		}
		clientsManager.removeConnection(this);
	}
	
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
