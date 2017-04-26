package program.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class decodes all incoming JSONObjects and encodes and sends return-JSONs
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class CommandsManager {
	/**
	 * This interface is used to allows the stringToFunction map to refer to the different methods of this class
	 */
	public interface Command{
		void doFunction(JSONObject obj, ClientConnection client);
	}
	//Refference objects
	private Map<String, Command> stringToFunction = new HashMap<String, Command>();
	private ClientsManager clientsManager;
	
	/**
	 * This constructor sets up the reference elements
	 * @param clientsManager The {@code ClientsManager} that allows this class to get refferences to the clients that are connected to the server
	 */
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
		stringToFunction.put("EndLecture", 
				(JSONObject obj, ClientConnection client) -> endLecture(obj, client));
	}

	/**
	 * This method analyzes a given JSONObject and passes it to the correct handler-method
	 * @param obj A {@code JSONObject} with data to be passed to the method. Only the key {@code "Function"} is used here
	 * @param client A {@code ClientConnection} referencing the client that sent the object
	 */
	public void analyzeFunction(JSONObject obj, ClientConnection client){
		try {
			stringToFunction.get(obj.get("Function")).doFunction(obj, client);;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method removes a client with the lecturer role from a live lecture they are currently set to be doing
	 * @param obj A {@code JSONObject} containing data the method needs <i>NO DATA IS NEEDED IN THIS METHOD</i>
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
	private void endLecture(JSONObject obj, ClientConnection client){
		clientsManager.removeLecturerFromLecture(client);
	}
	
	/**
	 * This finds the id of a currently live lecture, and returns it via the {@code ClientConnection}
	 * @param obj A {@code JSONObject} containing data the method needs <i>NO DATA IS NEEDED IN THIS METHOD</i>
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	
	/**
	 * This method gets a given number of the newest questions for a given lecture id
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"QuestionAmount" = int</pre>
	 * <pre>"ClassID" = String</pre>
	 * <pre>"LectureID" = int</pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	}
	
	/**
	 * This method sets a given role to a given client
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"Role" = String</pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	
	/**
	 * This method fetches all subject codes from the database
	 * @param obj A {@code JSONObject} containing data the method needs <i>NO DATA NEEDED FOR THIS MEHTOD</i>
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	
	/**
	 * This checks if a lecture is happening in a given subject
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"Class" = String</pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	
	/**
	 * This method sets up a lecture in a given subject
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"ClassID" = String</pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
	private void createLecture(JSONObject obj, ClientConnection client){
		try{
			System.out.println("create new lecture with class code: " + obj.getString("ClassID"));
			int lectureID = clientsManager.main.getDatabase().createNewLecture(obj.getString("LectureName"), obj.getString("ClassID"));
			//clientsManager.main.getDatabase().createNewLecture(obj.getString("LectureName"), obj.getString("ClassID"));
			clientsManager.addLecturerToLecture(client, obj.getString("ClassID"));
			client.setLectureID(lectureID);
			//client.setLectureID(clientsManager.main.getDatabase().getLiveLectureID(obj.getString("ClassID")));
		} catch (JSONException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method adds a time-stamp for lost students in the database, and informs the lecturer of a given lecture that a student is lost.
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"ClassID" = String</pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	
	/**
	 * This method adds a student to a lecture, updates the database, and informs the lecturer
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"ClassID" = String </pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	
	/**
	 * This method changes the score of a given question by a given increment, and informs all clients in the lecture 
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"ScoreChange" = int </pre>
	 * <pre>"QuestionID" = int </pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	
	/**
	 * This method posts a new question in a given lecture to the database, and informs all clients in the lecture
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"Question" = String</pre>
	 * <pre>"ClassID" = String</pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
			// Using existing functions to just get the latest question we just posted in the database
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
	
	/**
	 * This method fetches all lectures that has happened in a given lecture, and sends them back to the client
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"ClassID" = String</pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
	
	/**
	 * This method gets all info on a  given lecture, including questions, lostMe time-stamps and general info
	 * <br>The keys used by the {@code JSONObject} is as follows:
	 * <pre>"ClassID" = String</pre>
	 * <pre>"LectureID" = int</pre>
	 * @param obj A {@code JSONObject} containing data the method needs
	 * @param client A {@code ClientConnection} referencing the client that sent the command
	 */
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
