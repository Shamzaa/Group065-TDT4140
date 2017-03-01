package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



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
			System.out.println(query);
			if (stmt.execute(query)) {
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
			String query = "SELECT question, time, rating FROM questions where lecture_id = '" + lecture_id + 
					"' order by time desc";
			if (stmt.execute(query)) {
				try (ResultSet rs = stmt.getResultSet();) {
					while (rs.next() && (numberOfQuestions > 0)) {
						Map<String, String> result = new HashMap<String, String>();
						String question = rs.getString(1);
						String time = rs.getString(2);
						String rating = rs.getString(3);
						result.put("question", question);
						result.put("time", time);
						result.put("rating", rating);
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
