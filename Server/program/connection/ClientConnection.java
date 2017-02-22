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
	// references to external objects to help with features
	private CommandsManager comManager;
	
	// connection atributes
	private Socket clientSocket;
	private String role;
	// in and out data channels
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
		System.out.println("New connection!");
		
		try{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		
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
			}
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	

}
