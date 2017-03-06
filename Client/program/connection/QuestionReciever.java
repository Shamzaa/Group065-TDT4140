package program.connection;

import org.json.JSONObject;

public interface QuestionReciever {
	//Tell the server to send the numberOfQuestions most recent questions in the database
	public void fetchQuestions(int numberOfQuestions);
	//Add questions as they are sent back
	public void recieveQuestions(JSONObject obj);
	
}
