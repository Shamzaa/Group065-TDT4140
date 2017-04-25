package program.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;


/**
 * 
 * The main Class used to handle everything related to the server, 
 * from sending info and getting the streams used for listening for info from the server
 * @author Erling Ihlen
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class ServerManager {
	Socket client;
	private BufferedReader in;
	private PrintWriter out;
	
	
	/**
	 * Starts the connection to the server
	 * @param serverAdress String that is the IP address
	 * @param portNumber the portnumber the server has open for the client
	 */
	public ServerManager(String serverAdress, int portNumber){
		try{
			client = new Socket(serverAdress, portNumber);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
			
		} catch (IOException e){
			System.out.println(e);
		}
		
	}
	/**
	 * Method used to send a json object to the server.
	 * Makes the jsonobject into a string, which can be sent over socket
	 * @param obj The JSON object with all the info the client wants to send
	 */
	public void sendJSON(JSONObject obj){
		String json_data = obj.toString();
		out.println(json_data);
		
	}
	/**
	 * Getter to get the Socket object related to the server.
	 * @return socket object connected to the server
	 */
	public Socket getSocket(){
		return client;
	}
	/**
	 * Getter to get the input stream of the socket
	 * @return input stream of the socket
	 */
	public BufferedReader getInputStream(){
		return in;
	}
	/**
	 * getter to get the output stream of the socket
	 * @return output stream of the socket.
	 */
	public PrintWriter getOutputStream(){
		return out;
	}
}
