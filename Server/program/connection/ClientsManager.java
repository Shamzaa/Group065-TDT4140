package program.connection;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

public class ClientsManager {
	private Collection<ClientConnection> clientsConnected;
	private Map<String, ClientConnection> classIDToConnection;
	private CommandsManager comManager;
	
	
	
	public ClientsManager(int portNumber){
		comManager = new CommandsManager(this);
		classIDToConnection = new HashMap<String, ClientConnection>();
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		
		
		Runnable serverTask = new Runnable(){
			@Override
			public void run(){
				try{
					ServerSocket serverSocket = new ServerSocket(portNumber);
					System.out.println("Waiting for clients...");
					while(true){
						Socket clientSocket = serverSocket.accept();
						clientProcessingPool.submit(new ClientConnection(clientSocket, comManager));
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
	
	public Collection<ClientConnection> getClientsConnected(){
		return clientsConnected;
	}
	
	public void addLecturerToLecture(ClientConnection client, String classID){
		if(!doesLectureExcist(classID)){
			classIDToConnection.put(classID, client);
		}else{
			// TODO: return error message to client requesting classID
		}
	}
	
	public void sendInfoToLecturer(JSONObject obj, String classID){
		if(doesLectureExcist(classID)){
			// is case sensetive
			classIDToConnection.get(classID).sendJSON(obj);
		}else{
			throw new IllegalArgumentException("No lecturer holding a lecture in " + classID);
		}
	}
	
	public boolean doesLectureExcist(String classID){
		// is case sensetive
		return classIDToConnection.containsKey(classID);
	}
	
	public ClientConnection getLecturer(String classID){
		return classIDToConnection.get(classID);
	}
	
}
