package program.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * Class with static methods to return the most basic needs from the server
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 */
public class ServerRequests {

	
	/**
	 * Static method used to get all the subject codes the client can connect to
	 * @param sm The servermanager object handling the server connection
	 * @return a list of all the subject codes from the database
	 */
	public static ObservableList<String> getAllSubjectCodes(ServerManager sm){
		JSONObject obj = new JSONObject();
		try {
			obj.put("Function", "GetAllSubjectCodes");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sm.sendJSON(obj);
		
		BufferedReader in = sm.getInputStream();
		List<String> retList = new ArrayList<>();
		while(true){
			System.out.println("Listens for reply, to get all subject codes");
			String input;
			try{
				input = in.readLine();
				JSONObject reply = new JSONObject(input);
				JSONArray strList = reply.getJSONArray("SubjectList");
				System.out.println("Recieved reply:" + strList);
				for (int i = 0; i < strList.length(); i++) {
					retList.add(strList.getString(i));
				}
				return FXCollections.observableArrayList(retList);
				
			} catch (JSONException e) {
				// TODO: handle exception
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * static method to request from the server to see if a lecture 
	 * is being held in requested classID. This is used as a check to see wether
	 * a client can proceed to a new view.
	 * @param sm the server manager handling the connection to the database
	 * @param classID the class code
	 * @return a boolean confirming if a lecture is being held with the suggested class code
	 */
	public static boolean serverHasLecture(ServerManager sm, String classID){
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
