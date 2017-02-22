package program.connection;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandsManager {
	
	public interface Command{
		void doFunction(JSONObject obj, ClientConnection client);
	}
	
	private Map<String, Command> stringToFunction = new HashMap<String, Command>();
	
	
	public CommandsManager(){
		// Add supported functions from jsonobject
		stringToFunction.put("AssignRole", 
				(JSONObject obj, ClientConnection client) -> assignRoleToClient(obj, client)
		);
	}
	
	
	public void analyzeFunction(JSONObject obj, ClientConnection client){
		try {
			//System.out.println(stringToFunction.containsKey(obj.get("Function")));
			//System.out.println(obj.get("Function"));
			stringToFunction.get(obj.get("Function")).doFunction(obj, client);;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	// functions that we can do
	private void assignRoleToClient(JSONObject obj, ClientConnection client){
		try {
			client.setRole(obj.getString("Role"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
