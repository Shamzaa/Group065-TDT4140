package program.connection.listeners;

import org.json.JSONObject;

public interface QuestionReciever {
	/**
	 * Tell the server to send the most recent questions in the database
	 * @param numberOfQuestions The amount of recently asked question to receive
	 */
	public void fetchQuestions(int numberOfQuestions);
	
	/**
	 * Receives and displays the returned questions
	 * @param obj A JSONObject containing a list of questions and various meta-data
	 */
	public void recieveQuestions(JSONObject obj);
	
	/**
	 * Gets a specific ID for this live lecture so the client receives the correct questions
	 */
	public void fetchLiveLectureID();
	
	/**
	 * Receives and saves the returned lectureID
	 * @param ID The received live lecture ID
	 */
	public void setLiveLectureID(int ID);
	
	/**
	 * Get notified that questions has changed score
	 * @param questionID The question that changed its score
	 * @param newScore   The new score the question has
	 */
	public void updateQuestionScore(int questionID, int newScore);
	
}
