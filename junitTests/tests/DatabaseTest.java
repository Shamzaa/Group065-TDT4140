package tests;

import java.util.ArrayList;
import java.util.Map;

import database.Database;

public class DatabaseTest extends junit.framework.TestCase {
	Database db = null;
	
	@Override
	public void setUp() {
		db = new Database();
		assertTrue(db instanceof Database);
	}
	
	public void testConnection() {
		assertTrue(db.connect()); 
	}
	
	public void testSporring() {
		db.postNewQuestion("#test", 1);
		ArrayList<Map<String, String>> question = db.getLastestQuestions(1, 1);
		assertEquals("#test", question.get(0).get("question"));
		assertTrue(question.get(0).get("time") instanceof String);
		assertEquals(0, question.get(0).get("rating"));		
	}
	
	public void tearDown() {
		db.close();
	}
}
