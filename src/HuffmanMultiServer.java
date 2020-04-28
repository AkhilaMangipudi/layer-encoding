import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class HuffmanMultiServer {

    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 8080;

    public static void main(String args[]) throws IOException, ClassNotFoundException{
        //create the socket server object
        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates

	HuffmanCompress huffmanCompress = new HuffmanCompress();
        while(true){
	    Socket socket = null;
	    try {
            //creating socket and waiting for client connection
            socket = server.accept();
	    System.out.println("A new client is connected..");

	    //Obtain the input and output streams
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

	    System.out.println("Assigning a new thread for this client");
	    Thread t = new ClientHandler(socket, ois, oos, huffmanCompress);

	    //Invoking the start() method
	    t.start();
	    } catch (Exception e) {
		socket.close();
	    }
            //socket.close();
        }
        //System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        //server.close();
    }

}

class ClientHandler extends Thread {
	
	final ObjectInputStream ois;
	final ObjectOutputStream oos;
	final Socket socket;
	final HuffmanCompress huffmanCompress;

	//Constructor
	public ClientHandler(Socket s, ObjectInputStream ois, ObjectOutputStream oos, HuffmanCompress huffmanCompress) {
		this.ois = ois;
		this.oos = oos;
		this.socket = s;
		this.huffmanCompress = huffmanCompress;
	}
	
	@Override
	public void run() {
		
		String receivedMessage;
		String resultMessage;
		while(true) {
			try {
				receivedMessage = (String) this.ois.readObject();
				System.out.println("File(layer) for which we need the encoding tree is  " + receivedMessage);
            			if(receivedMessage.equalsIgnoreCase("exit")) {
                			this.oos.writeObject("Hi Client" + " Exiting");
                			this.ois.close();
                			this.oos.close();
                			this.socket.close();
               				break;
            			}
				//Call the encoder function here
				List<String> resultList = this.huffmanCompress.compressFile(receivedMessage);
				resultMessage = resultList.get(0) + "," + resultList.get(1);
				//Write object to output stream
				this.oos.writeObject(resultMessage);
				this.ois.close();
				this.oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found exception");
			}
		}
	}
}
