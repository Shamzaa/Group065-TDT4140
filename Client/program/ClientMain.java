package program;
import program.connection.*;
public class ClientMain {
	private ServerManager serverManager;
	
	private void init(){
		serverManager = new ServerManager("10.22.7.157", 2222);
	}
	
	public static void main(String[] args){
		ClientMain main = new ClientMain();
		main.init();
	}
}
