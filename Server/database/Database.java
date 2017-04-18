package database;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;



/**
 * @author pgjerstad
 *
 * For å bruke koblingen mot MySQL må alle som skal kjøre ServerMain, egentlig Database, ha mysql-connector-java-5.1.40-bin.jar på classpath.
 * 
 * jdbc-URLen krever DatabaseKey.java som ikke er sjekket inn i git.
 * 
 */


public class Database implements AutoCloseable {
	private Connection conn = null;
	private static String KEY_URL = DatabaseKey.KEY_URL;
	
	
	// delete this
	public static void main(String[] args) throws Exception {  // Denne main-klassen eksisterer kun for testformål.
		try (Database db = new Database()) {   // Fordi den implementerer AutoCloseable, vil den automatisk close.
			db.connect();
			// db.postNewQuestion("Hei, hei hei. Dette er et spørsmål", 1);
			db.testSporring();
		}
	}
	
	public boolean connect() {
		// Class.forName("com.mysql.jdbc.Driver");  Not needed if Eclipse?
		try {
			conn = DriverManager.getConnection(KEY_URL);
		}
		catch (SQLException ex) {
			System.out.println("SQLEcxeption: " + ex.getMessage());
			return false;
		}
		return true;
	}
	
	public void testSporring() {
		try (Statement stmt = conn.createStatement()) {
			String query = "SELECT question, time FROM questions";
			if (stmt.execute(query)) {
				try (ResultSet rs = stmt.getResultSet();) {
					while (rs.next()) {
						String question = rs.getString(1);
						String time = rs.getString(2);
						System.out.println(question + " - " + time);
					}					
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
/**
 * postNewQuestion() takes in the question as a string and the lecture_id as an int, and posts
 * the question to the database. Returns true if successful, false otherwise.
 */
	
	public boolean postNewQuestion(String question, int lecture_id) {
		try (Statement stmt = conn.createStatement()) {
			String query = "insert into questions(question, lecture_id, time) values ('" + question + "'," + lecture_id + ", NOW());";
			if (!stmt.execute(query)) {
				return true;
			}					
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public boolean executeStatement(String statement_as_String){
		try (Statement stmt = conn.createStatement()) {
			if (stmt.execute(statement_as_String)) {
				return true;
			}					
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}


	/**
	 * Takes in lecture_id as in, and the number of desired questions.
	 * Returns an ArrayList of dictionaries with keys: question, time, rating
	 */

	
	public ArrayList<Map<String, String>> getLastestQuestions(int lecture_id, int numberOfQuestions) {
		ArrayList<Map<String, String>> questions = new ArrayList<>();
		try (Statement stmt = conn.createStatement()) {
			String query = "SELECT question, time, rating, id FROM questions where lecture_id = '" + lecture_id + 
					"' order by time desc";
			if (stmt.execute(query)) {
				try (ResultSet rs = stmt.getResultSet();) {
					while (rs.next() && (numberOfQuestions > 0)) {
						Map<String, String> result = new HashMap<String, String>();
						//
						
						String question = rs.getString(1);
						String time = rs.getString(2);
						String rating = rs.getString(3);
						String id = rs.getString(4);
						result.put("question", question);
						result.put("time", time);
						result.put("rating", rating);
						result.put("id", id);
						questions.add(result);
						numberOfQuestions --;
					}					
				}
				return questions;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return questions;
	}
	
	// gets stats over latest lectures
	public ArrayList<Map<String, String>> getLatestLectures(String classID){
		ArrayList<Map<String, String>> lectures = new ArrayList<>();
		try (Statement stmt = conn.createStatement()) {
			String query = "SELECT name, studentsJoined, id FROM lecture where subject_code = '" + classID + 
					"'";
			if (stmt.execute(query)) {
				try (ResultSet rs = stmt.getResultSet();) {
					while (rs.next()) {
						Map<String, String> result = new HashMap<String, String>();
						String lectureName = rs.getString(1);
						String studentsJoined = Integer.toString(rs.getInt(2));
						String id = Integer.toString(rs.getInt(3));
						result.put("lectureName", lectureName);
						result.put("id", id);
						result.put("studentsJoined", studentsJoined);
						lectures.add(result);
					}					
				}
				return lectures;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return lectures;
		
	}
	
	// looks at most recent lecture object in the database with the given lecture code
	public int getLiveLectureID(String classID){
		int lectureID = 0;
		
		try (Statement stmt = conn.createStatement();){
			String query = "SELECT id FROM lecture WHERE subject_code='" + classID.toUpperCase() + "' ORDER BY id DESC LIMIT 1";
			
			if(stmt.execute(query)){
				ResultSet rs = stmt.getResultSet();
				rs.next();
				lectureID = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lectureID;
	}
	
	// creates a new lecture, and returns the lectureID in the database.
	public int createNewLecture(String lectureName, String classID){
		// only doable if classID excists in database.

		Time timeStamp = Time.valueOf(LocalTime.now());
		Date date = Date.valueOf(LocalDate.now());
		try (Statement stmt = conn.createStatement()) {
			String query = "insert into lecture(name, subject_code, studentsJoined, start, date) values ('"+ lectureName +"', '" + classID.toUpperCase() + "',0, '"+ timeStamp +"', '"+ date +"');";
			if (stmt.execute(query)) {
				return getLiveLectureID(classID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return 0;
	}
	

	public void setEndLecture(int lectureID){

		Time timeStamp = Time.valueOf(LocalTime.now());
		try(Statement stmt = conn.createStatement()){
			String query = "UPDATE `lecture` SET`stop`='"+ timeStamp +"' WHERE `id`=" + lectureID + ";";
			stmt.execute(query);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	// creates a new subject/class
	public boolean createNewSubject(String classID, String name){		
		try (Statement stmt = conn.createStatement()) {
			String query = "insert into subjects(code, name) values ('" + classID.toUpperCase() + "', '" + name + "');";
			stmt.execute(query);
			return true;				
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	// can vote for a question and change the score. Takes in the question ID, and a boolean to upvote(true) or downvote(false)
	
	public void voteQuestion(int questionID, int val){
		try(Statement stmt = conn.createStatement()){
			String query = "UPDATE `questions` SET `rating`=`rating` + "+ Integer.toString(val) + " WHERE id="+questionID+ ";";
			stmt.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getScoreQuestion(int questionID){
		try(Statement stmt = conn.createStatement()){
			String query = "SELECT `rating` FROM `questions` WHERE `id`="+questionID+";";
			if(stmt.execute(query)){
				ResultSet rs = stmt.getResultSet();
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public ArrayList<String> getAllSubjectCodes(){
		ArrayList<String> sCodes = new ArrayList<>();
		try(Statement stmt = conn.createStatement()){
			String query = "SELECT `code` FROM `subjects`";
			if(stmt.execute(query)){
				ResultSet rs = stmt.getResultSet();
				while(rs.next()){
					sCodes.add(rs.getString(1));
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return sCodes;
	}
	
	public void addStudentCountToLecture(int lectureID){
		try(Statement stmt = conn.createStatement()){
			String query = "UPDATE `lecture` SET `studentsJoined`=`studentsJoined`+1 WHERE `id`=" + lectureID + ";";
			stmt.executeUpdate(query);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void createYouLostMe(int lectureID){
		Time timeStamp = Time.valueOf(LocalTime.now());
		try(Statement stmt = conn.createStatement()){
			String query = "INSERT INTO `youlostme`(`lectureID`, `timeStamp`) VALUES ("+ lectureID +",'"+ timeStamp +"');";
			stmt.execute(query);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Timestamp> getLostMeTimestamps(int lectureID){
		ArrayList<Timestamp> retArr = new ArrayList<>();
		try(Statement stmt = conn.createStatement()){
			String query = "SELECT timeStamp FROM youlostme WHERE lectureID="+String.valueOf(lectureID);
			if(stmt.execute(query)){
				ResultSet rs = stmt.getResultSet();
				while(rs.next()){
					retArr.add(rs.getTimestamp(1));
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return retArr;
	}
	
	public HashMap<String, String> getLectureStats(int lectureID){
		HashMap<String, String> retMap = new HashMap<>();
		try (Statement stmt = conn.createStatement()){
			String query = "SELECT name, studentsJoined, start, stop FROM lecture WHERE id = " + String.valueOf(lectureID);
			//				SELECT name, studentsJoined, start, stop FROM lecture WHERE id = 170
			System.out.println("QUERY >> "+ query);
			if(stmt.execute(query)){
				try(ResultSet rs = stmt.getResultSet()){
					while(rs.next()){
						retMap.put("name", rs.getString(1));
						if(rs.wasNull()){
							retMap.replace("name", "");
						}
						retMap.put("studentsJoined", rs.getString(2));
						if(rs.wasNull()){
							retMap.replace("studentsJoined", "");
						}
						retMap.put("start", rs.getString(3));
						if(rs.wasNull()){
							retMap.replace("start", "");
						}
						retMap.put("stop", rs.getString(4));
						if(rs.wasNull()){
							retMap.replace("stop", "");
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	
	@Override
	public void close() {
		try {
			if (conn != null)
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
}
