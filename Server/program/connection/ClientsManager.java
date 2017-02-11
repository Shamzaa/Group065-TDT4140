package program.connection;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientsManager {
	private Collection<ClientConnection> clientsConnected;
	
	public ClientsManager(int portNumber){
		
		
		
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		
		Runnable serverTask = new Runnable(){
			@Override
			public void run(){
				try{
					ServerSocket serverSocket = new ServerSocket(portNumber);
					System.out.println("Waiting for clients...");
					while(true){
						Socket clientSocket = serverSocket.accept();
						clientProcessingPool.submit(new ClientConnection(clientSocket));
					}
				}catch(IOException e){
					System.out.println("couldn't process client request");
					e.printStackTrace();
				}
			}
		};
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}
	
	public Collection<ClientConnection> getClientsConnected(){
		return clientsConnected;
	}

}
