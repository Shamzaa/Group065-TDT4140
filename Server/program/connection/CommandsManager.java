package program.connection;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jdk.nashorn.internal.scripts.JS;
import program.database.Database;

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
		stringToFunction.put("GetLiveLectureID",
				(JSONObject obj, ClientConnection client) -> getLiveLectureID(obj, client));
		stringToFunction.put("VoteQuestion",
				(JSONObject obj, ClientConnection client) -> voteQuestion(obj, client));
		stringToFunction.put("GetAllSubjectCodes", 
				(JSONObject obj, ClientConnection client) -> getAllSubjectCodes(obj, client));
		stringToFunction.put("FetchLectures", 
				(JSONObject obj, ClientConnection client) -> getLatestLectures(obj, client));
		stringToFunction.put("reviewLecture", 
				(JSONObject obj, ClientConnection client) -> reviewLecture(obj, client));
		stringToFunction.put("FetchLostMeTimeStamps",
				(JSONObject obj, ClientConnection client) -> fetchLostMeTimeStamps(obj, client));
		stringToFunction.put("EndLecture", 
				(JSONObject obj, ClientConnection client) -> endLecture(obj, client));
	}


	public void analyzeFunction(JSONObject obj, ClientConnection client){
		try {
			stringToFunction.get(obj.get("Function")).doFunction(obj, client);;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void endLecture(JSONObject obj, ClientConnection client){
		clientsManager.removeLecturerFromLecture(client);
	}
	
	private void fetchLostMeTimeStamps(JSONObject obj, ClientConnection client) {
		try {
			int lectureID = obj.getInt("lectureID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getLiveLectureID(JSONObject obj, ClientConnection client) {
		
		try {
			int liveID = client.getLectureID();
			
			JSONObject retObj = new JSONObject();
			retObj.put("Function", "SetLiveLectureID");
			retObj.put("LiveLectureID", liveID);
			client.sendJSON(retObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// functions that we can do
	private void getLatestQuestions(JSONObject obj, ClientConnection client){
		try {
			System.out.println("Fetching " + String.valueOf(obj.getInt("QuestionAmount")) + "questions for class " + obj.getString("ClassID") +"["+obj.getInt("LectureID")+"]");
			ArrayList<Map<String, String>> retArr = clientsManager.main.getDatabase().getLastestQuestions(obj.getInt("LectureID"), obj.getInt("QuestionAmount"));
			
			
			JSONObject retObj = new JSONObject();
			retObj.put("Function", "addQuestions");
			retObj.put("QuestionAmount", obj.getInt("QuestionAmount"));
			retObj.put("List", new JSONArray(retArr));
			
			client.sendJSON(retObj);			
						
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//db.close();
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
	
	private void getAllSubjectCodes(JSONObject obj, ClientConnection client) {
		JSONObject reply = new JSONObject();
		System.out.println("Fetching all subject codes");
		//TODO this should fetch from the database and get fill the array
		System.out.println("Fetching subject codes");
		ArrayList<String> retArr = new ArrayList<>();
		retArr.addAll(clientsManager.main.getDatabase().getAllSubjectCodes());
		System.out.println("fetched" + retArr);
		//----------------------
		try {
			reply.put("SubjectList", new JSONArray(retArr));
			client.sendJSON(reply);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void isLectureHappening(JSONObject obj, ClientConnection client){
		JSONObject reply = new JSONObject();

		try {
			System.out.println("requests to see if lecture excists: " + obj.getString("Class") + ": " + clientsManager.doesLectureExist(obj.getString("Class")));
			reply.put("ClassExcist", clientsManager.doesLectureExist(obj.getString("Class")));
			client.sendJSON(reply);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createLecture(JSONObject obj, ClientConnection client){
		try{
			System.out.println("create new lecture with class code: " + obj.getString("ClassID"));
			clientsManager.main.getDatabase().createNewLecture(obj.getString("LectureName"), obj.getString("ClassID"));
			clientsManager.addLecturerToLecture(client, obj.getString("ClassID"));
			client.setLectureID(clientsManager.main.getDatabase().getLiveLectureID(obj.getString("ClassID")));
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
			clientsManager.main.getDatabase().createYouLostMe(lecturer.getLectureID());
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
			
			clientsManager.main.getDatabase().addStudentCountToLecture(client.getLectureID());
			
			
			lecturer.sendJSON(notification);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void voteQuestion(JSONObject obj, ClientConnection client) {
		try {
			int val = obj.getInt("ScoreChange");
			int questionID = obj.getInt("QuestionID");
			// updates score in database
			clientsManager.main.getDatabase().voteQuestion(questionID, val);
			
			// notifies all clients connected to this lecture with update score of a question
			int score = clientsManager.main.getDatabase().getScoreQuestion(questionID);
			JSONObject questionScore = new JSONObject();
			questionScore.put("Function", "updateScore");
			questionScore.put("QuestionID", questionID);
			questionScore.put("Score", score);
			
			for(ClientConnection c : clientsManager.getClientsConnected()){
				if(c.getLectureID() == client.getLectureID()){
					c.sendJSON(questionScore);
				}
			}
		} catch (JSONException e) {
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
			retObj.put("QuestionAmount", 1);
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
	
	private void getLatestLectures(JSONObject obj, ClientConnection client){
		try {
			System.out.println("Lecturer has requested to get an overview of all lectures in class " + obj.getString("ClassID"));
			ArrayList<Map<String, String>> retArr = clientsManager.main.getDatabase().getLatestLectures(obj.getString("ClassID"));
			JSONObject retObj = new JSONObject();
			retObj.put("Function", "addLectures");
			retObj.put("List", new JSONArray(retArr));
			client.sendJSON(retObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void reviewLecture(JSONObject obj, ClientConnection client){
		try {
			int id = obj.getInt("LectureID"); 
			System.out.println("Lecturer has requested to get a review of lecture " + obj.getString("ClassID") + " | " + obj.getInt("LectureID"));
			getLatestQuestions(obj, client);
			System.out.println(">>>  Questions done");
			JSONObject retObj = new JSONObject();
			retObj.put("Function", "lectureReview");
			retObj.put("StampList", clientsManager.main.getDatabase().getLostMeTimestamps(id)); //JSONArray
			retObj.put("JSONStats", clientsManager.main.getDatabase().getLectureStats(id));		//JSONObject		
			System.out.println("Sending: " + retObj.toString());
			client.sendJSON(retObj);
			System.out.println("Sent!!");
			// attributes of lecture (name, students lost, students connected)
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
