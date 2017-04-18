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

public class ClientsManager {
	private Collection<ClientConnection> clientsConnected;
	private Map<String, ClientConnection> classIDToConnection;
	private CommandsManager comManager;
	public ServerMain main;
	
	// made so that we can add this clientsManager to parameters inside serverTask
	private ClientsManager clientsManager;
	
	
	
	public ClientsManager(int portNumber, ServerMain main){
		clientsConnected = new ArrayList<ClientConnection>();
		clientsManager = this;
		comManager = new CommandsManager(this);
		this.main = main;
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
	
	public Collection<ClientConnection> getClientsConnected(){
		return clientsConnected;
	}
	
	public void addLecturerToLecture(ClientConnection client, String classID){
		if(!doesLectureExist(classID)){
			classIDToConnection.put(classID, client);
			client.setClassID(classID);
		}else{
			// TODO: return error message to client requesting classID
		}
	}
	
	public void removeLecturerFromLecture(ClientConnection client){
		if(doesLectureExist(client.getClassID()) && classIDToConnection.get(client.getClassID()) == client){
			classIDToConnection.remove(client.getClassID());
			clientsManager.main.getDatabase().setEndLecture(client.getLectureID());
		}
	}
	
	public void removeConnection(ClientConnection client){
		clientsConnected.remove(this);
		
	}
	public void addClientToCollection(ClientConnection client){
		clientsConnected.add(client);
	}
	
	public void sendInfoToLecturer(JSONObject obj, String classID){
		if(doesLectureExist(classID)){
			// is case sensitive
			classIDToConnection.get(classID).sendJSON(obj);
		}else{
			throw new IllegalArgumentException("No lecturer holding a lecture in " + classID);
		}
	}
	
	public boolean doesLectureExist(String classID){
		// is case sensitive
		return classIDToConnection.containsKey(classID);
	}
	
	public ClientConnection getLecturer(String classID){
		return classIDToConnection.get(classID);
	}
	
}
