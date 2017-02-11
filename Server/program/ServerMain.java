package program;
import program.connection.*;
public class ServerMain {
	private ClientsManager clientsManager;
	
	
	private void init(int portNumber){
		clientsManager = new ClientsManager(portNumber);
	}
	
	public static void main(String[] args){
		ServerMain main = new ServerMain();
		main.init(2222);
	}
}
