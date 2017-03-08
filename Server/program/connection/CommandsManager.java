package program.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.Database;

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
		stringToFunction.put("GetLatestQuestions",
				(JSONObject obj, ClientConnection client) -> getLatestQuestions(obj, client));
		stringToFunction.put("NewQuestion", 
				(JSONObject obj, ClientConnection client) -> newQuestion(obj, client));
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
	private void getLatestQuestions(JSONObject obj, ClientConnection client){
		try {
			System.out.println("Fetching " + String.valueOf(obj.getInt("QuestionAmount")) + "questions for class " + obj.getString("ClassID"));
			Database db =  new Database();
			db.connect();
			ArrayList<Map<String, String>> retArr = db.getLastestQuestions(1, obj.getInt("QuestionAmount"));
			System.out.println(retArr);
			
			db.close();
			
			//TODO get retArr back to requester
			JSONObject retObj = new JSONObject();
			retObj.put("Function", "addQuestions");
			retObj.put("QuestionAmount", obj.getInt("QuestionAmount"));
			retObj.put("List", new JSONArray(retArr));
			
			client.sendJSON(retObj);
			
						
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
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
			clientsManager.main.getDatabase().createNewLecture(obj.getString("ClassID"));
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
			client.setLectureID(clientsManager.main.getDatabase().getLiveLectureID(obj.getString("ClassID")));
			
			notification.put("Function", "JoinedLecture");
			
			lecturer.sendJSON(notification);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void newQuestion(JSONObject obj, ClientConnection client){
		// student submitted new question.
		try{
			System.out.println("Student has submitted new question: " + obj.getString("Question") + ". To class :" + obj.getString("ClassID"));
			clientsManager.main.getDatabase().postNewQuestion(obj.getString("Question"), client.getLectureID());
			/* EXAMPLE QUERY:
			  		SELECT * FROM lecture 
					WHERE subject_code='TDT4100'
					ORDER BY id DESC LIMIT 1
			 */
			
			// send the new question to all clients
			// Using excisting functions to just get the latest question we just posted in the database
			ArrayList<Map<String, String>> retArr = clientsManager.main.getDatabase().getLastestQuestions(client.getLectureID(), 1);
			JSONObject retObj = new JSONObject();
			retObj.put("Function", "addQuestions");
			retObj.put("QuestionAmount", obj.getInt("QuestionAmount"));
			retObj.put("List", new JSONArray(retArr));
			
			// Iterates through connected clients, and sends the new question to people who are connected to the same lecture as the student who sent in the question
			for(ClientConnection c : clientsManager.getClientsConnected()){
				if(c.getLectureID() == client.getLectureID()){
					c.sendJSON(retObj);
				}
			}
			
			
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

}
