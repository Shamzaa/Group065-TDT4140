package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;



/**
 * @author pgjerstad
 *
 * For å bruke koblingen mot MySQL må alle som skal kjøre ServerMain, egentlig Database, ha mysql-connector-java-5.1.40-bin.jar på classpath.
 * 
 * jdbc-URLen krever DatabaseKey.java som ikke er sjekket inn i git.
 * 
 */


public class Database implements AutoCloseable {
	Connection conn = null;
	private static String KEY_URL = DatabaseKey.KEY_URL;
	
	public static void main(String[] args) throws Exception {  // Denne main-klassen eksisterer kun for testformål.
		System.out.println("test");
		try (Database db = new Database()) {   // Fordi den implementerer AutoCloseable, vil den automatisk close.
			db.connect();
			db.postNewQuestion("Hei, hei hei. Dette er et spørsmål", 1);
		}
	}
	
	public void connect() throws Exception {
		// Class.forName("com.mysql.jdbc.Driver");  Not needed if Eclipse?
		try {
			conn = DriverManager.getConnection(KEY_URL);
		}
		catch (SQLException ex) {
			System.out.println("SQLEcxeption: " + ex.getMessage());
		}
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
	
	protected boolean postNewQuestion(String question, int lecture_id) {
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