package program.connection;

import org.json.JSONObject;

public interface LectureReciever {
	public void fetchLectures();
	public void recieveLectures(JSONObject obj);
}
