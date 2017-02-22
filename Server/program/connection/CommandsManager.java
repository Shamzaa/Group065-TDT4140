package program.connection;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandsManager {
	
	public interface Command{
		void doFunction(JSONObject obj, ClientConnection client);
	}
	
	private Map<String, Command> stringToFunction = new HashMap<String, Command>();
	private ClientsManager clientsManager;
	
	public CommandsManager(ClientsManager clientsManager){
		this.clientsManager = clientsManager;
		// Add supported functions from jsonobject
		stringToFunction.put("AssignRole", 
				(JSONObject obj, ClientConnection client) -> assignRoleToClient(obj, client));
		stringToFunction.put("ClassRunning", 
				(JSONObject obj, ClientConnection client) -> isLectureHappening(obj, client));
		stringToFunction.put("CreateNewLecture",
				(JSONObject obj, ClientConnection client) -> createLecture(obj, client));
		stringToFunction.put("LostMe",
				(JSONObject obj, ClientConnection client) -> studentLost(obj, client));
		stringToFunction.put("JoinLecture", 
				(JSONObject obj, ClientConnection client) -> studentJoin(obj, client));
	}
	
	
	public void analyzeFunction(JSONObject obj, ClientConnection client){
		try {
			//System.out.println(stringToFunction.containsKey(obj.get("Function")));
			//System.out.println(obj.get("Function"));
			stringToFunction.get(obj.get("Function")).doFunction(obj, client);;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	// functions that we can do
	private void assignRoleToClient(JSONObject obj, ClientConnection client){
		try {
			client.setRole(obj.getString("Role"));
			
			// test code to create a lecture with the id "test"
			if(obj.getString("Role").equals("Lecturer")){
				clientsManager.addLecturerToLecture(client, "test");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void isLectureHappening(JSONObject obj, ClientConnection client){
		JSONObject reply = new JSONObject();

		try {
			System.out.println("requests to see if lecture excists: " + obj.getString("Class") + ": " + clientsManager.doesLectureExcist(obj.getString("Class")));
			reply.put("ClassExcist", clientsManager.doesLectureExcist(obj.getString("Class")));
			client.sendJSON(reply);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createLecture(JSONObject obj, ClientConnection client){
		try{
			System.out.println("create new lecture with class code: " + obj.getString("ClassID"));
			clientsManager.addLecturerToLecture(client, obj.getString("ClassID"));
		} catch (JSONException e){
			
		}
	}
	
	private void studentLost(JSONObject obj, ClientConnection client){
		JSONObject notification = new JSONObject();

		try {
			System.out.println("Student lost in lecture in class " + obj.getString("ClassID"));
			ClientConnection lecturer = clientsManager.getLecturer(obj.getString("ClassID"));
			
			notification.put("Function", "StudentLost");
			
			lecturer.sendJSON(notification);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void studentJoin(JSONObject obj, ClientConnection client){
		JSONObject notification = new JSONObject();
		try {
			System.out.println("Student joined lecture in " + obj.getString("ClassID"));
			ClientConnection lecturer = clientsManager.getLecturer(obj.getString("ClassID"));
			
			notification.put("Function", "JoinedLecture");
			
			lecturer.sendJSON(notification);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
