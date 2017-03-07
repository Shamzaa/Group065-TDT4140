package program.connection;

import org.json.JSONObject;

public interface QuestionReciever {
	//Tell the server to send the numberOfQuestions most recent questions in the database
	public void fetchQuestions(int numberOfQuestions);
	//Add questions as they are sent back
	public void recieveQuestions(JSONObject obj);
	//Set a specific ID for this live lecture so the client recieves the correct questions
	public void fetchLiveLectureID();
	public void setLiveLectureID(int ID);
	
}
