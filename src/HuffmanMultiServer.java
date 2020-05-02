import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;

public class HuffmanMultiServer {
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 8080;

    public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
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
        }
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
            			CompressResult compressResult = huffmanCompress.compressLayer(receivedMessage, 1000); // 1000 is the block size
            			//write object to Socket
            			this.oos.writeObject(compressResult.getVersionId());
            			this.oos.writeObject(compressResult.getEncodings());
            			this.oos.writeObject(compressResult.getHashToBlockMap());
				this.ois.close();
				this.oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found exception");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}
}
