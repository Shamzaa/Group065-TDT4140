package program.connection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientConnection implements Runnable{
	private Socket clientSocket;
	private CommandsManager comManager;
	
	
	private String role;
	private BufferedReader in;
	private PrintWriter out;
	
	public ClientConnection(Socket clientSocket, CommandsManager comManager){
		this.clientSocket = clientSocket;
		this.comManager = comManager;
	}
	
	public void setRole(String role){
		System.out.println("role: '" + role + "' assigned to socket");
		this.role = role;
	}
	
	public void sendJSON(JSONObject obj){
		String json_data = obj.toString();
		out.println(json_data);
	}
	
	@Override
	public void run() {
		System.out.println("new connection!");
		
		// verify bla bla bla
		
		
		try{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		
			String recievedString;
			while(true){
				System.out.println("Listens for new input");
				String input = in.readLine();
				try{
					JSONObject obj = new JSONObject(input);
					comManager.analyzeFunction(obj, this);
				}catch(JSONException e){
					// data recieved wasn't a json object
					// close connection or ignore.
				}
				// listen to data from client and process into JSON into application/database/assign role(student/lecturer)
			}
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	

}
