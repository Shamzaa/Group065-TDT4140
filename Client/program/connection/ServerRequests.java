package program.connection;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerRequests {

	
	
	// static method to request from the server to see if a lecture is being held in requested classID.
	public static boolean serverHasClass(ServerManager sm, String classID){
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "ClassRunning");
			obj.put("Class", classID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		sm.sendJSON(obj);
		
		
		BufferedReader in = sm.getInputStream();
		while(true){
			System.out.println("Listens for reply from the server, to see if requested class excists");
			String input;
			try {
				input = in.readLine();
				JSONObject reply = new JSONObject(input);
				return reply.getBoolean("ClassExcist");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch(JSONException e) {
				// data recieved wasn't a json object
				// close connection or ignore.
			}
		}
	}


}
