package program.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;


public class ServerManager {
	Socket client;
	private BufferedReader in;
	private PrintWriter out;
	
	
	public ServerManager(String serverAdress, int portNumber){
		try{
			client = new Socket(serverAdress, portNumber);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
			
		} catch (IOException e){
			System.out.println(e);
		}
		
	}
	
	public void sendJSON(JSONObject obj){
		String json_data = obj.toString();
		out.println(json_data);
		
	}
	
	
	
	public Socket getSocket(){
		return client;
	}
	
	public BufferedReader getInputStream(){
		return in;
	}
	
	public PrintWriter getOutputStream(){
		return out;
	}
}
