package program.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import program.database.DatabaseKey;



/**
 * This class is used to connect and run SQL-queries on the database
 * To use this class, the library mysql-connector-java-5.1.40-bin.jar is needed
 * A URL to gain access is also needed, and is provided in the class DatabaseKey (not checked in to our github)
 * 
 * @author Peder Gjerstad
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class Database implements AutoCloseable {
	// Reference elements
	private Connection conn = null;
	private static String KEY_URL = DatabaseKey.KEY_URL;
	
	/**
	 * This method sets up a Connection object, and connects to to the database with the {@code KEY_URL}
	 * @return Returns {@code true} if the connection was successful, otherwise it returns {@code false}
	 */
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

	/**
	 * This method inserts a new question to the database 
	 * @param question A {@code String} containing the question text
	 * @param lecture_id An {@code integer} giving the id of the lecture where the question was asked
	 * @return Returns {@code true} if the query was was executed successfully, otherwise it returns {@code false}
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
	
	/**
	 * This mehtod executes a query given as a parameter
	 * @param statement_as_String A {@code String} that can be executed as a SQL query
	 * @return Returns {@code true} if the query was was executed successfully, otherwise it returns {@code false}
	 */
	@SuppressWarnings("static-access")
	public boolean executeStatement(String statement_as_String){
		try (Statement stmt = conn.createStatement()) {
			stmt.execute(statement_as_String);
			if (0 != stmt.EXECUTE_FAILED) {
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
	
	/**
	 * This method gets a specified amount of questions from the database and returns the result
	  * @param lecture_id An {@code integer} with the id of the lecture the questions should be selected from
	 * @param numberOfQuestions An {@code integer} with the amount of questions that should be returned
	 * @return Returns an {@code ArrayList<Map<String, String>>} consisting of the  {@code numberOfQuestions} newest questions. The list will be empty if the query was unsuccessful 
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
	
	/**
	 * This method gets all info from all lectures in the database, for a specified subject-code
	  * @param classID A {@code String} with the subject-code that the lectures should be retrieved from
	 * @return Returns an {@code ArrayList<Map<String, String>>} consisting of the lectures in the subject. The list will be empty if the query was unsuccessful
	 */
	public ArrayList<Map<String, String>> getLatestLectures(String classID){
		ArrayList<Map<String, String>> lectures = new ArrayList<>();
		try (Statement stmt = conn.createStatement()) {
			String query = "SELECT name, studentsJoined, id, date FROM lecture where subject_code = '" + classID + 
					"'";
			if (stmt.execute(query)) {
				try (ResultSet rs = stmt.getResultSet();) {
					while (rs.next()) {
						Map<String, String> result = new HashMap<String, String>();
						String lectureName = rs.getString(1);
						String studentsJoined = Integer.toString(rs.getInt(2));
						String id = Integer.toString(rs.getInt(3));
						Date date = rs.getDate(4);
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
						
						String dateString = sdf.format(date);
						result.put("lectureName", lectureName);
						result.put("id", id);
						result.put("studentsJoined", studentsJoined);
						result.put("date", dateString);
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
	
	/**
	 * Fetches the id of the latest lecture in a given subject-code
	 * @param classID A {@code String} giving the subject-code
	 * @return Returns an {@code integer}, which equals the ID of the lecture if the query executed successfully, and 0 otherwise
	 */
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
	
	/**
	 * This method creates a new lecture in a given subject
	 * @param lectureName 	A {@code String} with the name of the lecture
	 * @param classID		A {@code String} with the subject code the lecture should be tied to
	 * @return Returns an {@code integer} with the lecture id of the new lecture, if the query was successful. Otherwise it returns 0
	 */
	public int createNewLecture(String lectureName, String classID){
		// only doable if classID excists in database.

		Time timeStamp = Time.valueOf(LocalTime.now());
		Date date = Date.valueOf(LocalDate.now());
		try (Statement stmt = conn.createStatement()) {
			String query = "insert into lecture(name, subject_code, studentsJoined, start, date) values ('"+ lectureName +"', '" + classID.toUpperCase() + "',0, '"+ timeStamp +"', '"+ date +"');";
			System.out.println(query);
			int i = stmt.executeUpdate(query);
			if (i > 0) {
				return getLiveLectureID(classID);
			}
			System.out.println("query didnt work");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * This method updates a given lecture with a time-stamp in the stop-coloumn
	 * @param lectureID A {@code String} giving the id of the lecture
	 * @return Returns an {@code integer}, which equals 1 if the query executed successfully, and 0 otherwise
	 */
	public int setEndLecture(int lectureID){
		Time timeStamp = Time.valueOf(LocalTime.now());
		try(Statement stmt = conn.createStatement()){
			String query = "UPDATE `lecture` SET`stop`='"+ timeStamp +"' WHERE `id`=" + lectureID + ";";
			int i = stmt.executeUpdate(query);
			if(i > 0){
				return 1;				
			}
			return 0;
		}catch(SQLException e){
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * This method adds a new subject to the database
	 * @param classID A {@code String} with an UNIQUE subject code (ex: TMT4100)
	 * @param name A {@code String} giving a describing name for the subject (ex: "Program Development")
	 * @return Returns {@code true} if the query was was executed successfully, otherwise it returns {@code false}
	 */
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
	/**
	 * Updates and increases a given question's score by a value
	 * @param questionID An {@code integer} giving the id of the question that should be updated
	 * @param val An {@code integer} giving the amount the score should be increased by
	 * @return Returns an {@code integer}, which equals 1 if the query executed successfully, and 0 otherwise
	 */
	public int voteQuestion(int questionID, int val){
		try(Statement stmt = conn.createStatement()){
			String query = "UPDATE `questions` SET `rating`=`rating` + "+ Integer.toString(val) + " WHERE id="+questionID+ ";";
			int i = stmt.executeUpdate(query);
			if(i > 0){
				return 1;
			}
			return 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Fetches the score of a given question from the database
	 * @param questionID An {@code integer} giving the id of the question that should be checked
	 * @return Returns an {@code integer}, which equals the score if the query executed successfully, and 0 otherwise
	 */
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
	
	/**
	 * This method fetches all subject codes from the database
	 * @return An {@code ArrayList<String>} consisting of all the subject codes in the database.
	 * 		   If the query was unsuccessful, the array will be empty
	 */
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
	
	/**
	 * Increases the number of connected students in a given lecture	
	 * @param lectureID A {@code String} giving the id of the lecture where the amount should be updated
	 * @return Returns an {@code integer}, which equals 1 if the query executed successfully, and 0 otherwise
	 */
	public int addStudentCountToLecture(int lectureID){
		try(Statement stmt = conn.createStatement()){
			String query = "UPDATE `lecture` SET `studentsJoined`=`studentsJoined`+1 WHERE `id`=" + lectureID + ";";
			int i = stmt.executeUpdate(query);
			return i>0 ? 1 : 0;
		}catch(SQLException e){
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * This method saves a time-stamp for the lost student notifications in the database
	 * @param lectureID An {@code integer} giving the id of the lecture the time-stamp is tied to
	 * @return Returns an {@code integer}, which equals 1 if the query executed successfully, and 0 otherwise
	 */
	public int createYouLostMe(int lectureID){
		Time timeStamp = Time.valueOf(LocalTime.now());
		try(Statement stmt = conn.createStatement()){
			String query = "INSERT INTO `youlostme`(`lectureID`, `timeStamp`) VALUES ("+ lectureID +",'"+ timeStamp +"');";
			int i = stmt.executeUpdate(query);
			return i>0 ? 1 : 0;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * This method fetches all time-stamps for lost students, for a given lecture
	 * @param lectureID An {@code integer} giving the id of the lecture to get the time-stamps from
	 * @return Returns an {@code ArrayList<Timestamp>} of all the time-stamps that the given lecture had. Returns an empty array if the query was unsuccessful
	 */
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
	
	/**
	 * Gets all info about a lecture
	 * @param lectureID An {@code integer} giving the id of the lecture the statistics should be gotten from
	 * @return Returns {@code HashMap<String, String>} with the following keys: {@code "name", "studentsJoined", "start", "stop", "date"}
	 */
	public HashMap<String, String> getLectureStats(int lectureID){
		HashMap<String, String> retMap = new HashMap<>();
		try (Statement stmt = conn.createStatement()){
			String query = "SELECT name, studentsJoined, start, stop, date FROM lecture WHERE id = " + String.valueOf(lectureID);
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
						retMap.put("date", rs.getString(5));
						if(rs.wasNull()){
							retMap.replace("date", "");
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
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
