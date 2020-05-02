import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.security.NoSuchAlgorithmException;

/**
 * Class which represents the server(Registry) to which all the clients connect to get the layers
 */
public class HuffmanServer {
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
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String layerName = (String) ois.readObject();
            
	    System.out.println("Layer for which we need the encoding is  " + layerName);
            if(layerName.equalsIgnoreCase("exit")) {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("Hi Client" + " Exiting");
                ois.close();
                oos.close();
                socket.close();
		break;
            }

            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //Call the encoder function here
            CompressResult compressResult = huffmanCompress.compressLayer(layerName, 1000); // 1000 is the block size
            //write object to Socket
            oos.writeObject(compressResult.getVersionId());
	    oos.writeObject(compressResult.getEncodings());
	    oos.writeObject(compressResult.getHashToBlockMap());
            //close resources
            ois.close();
            oos.close();
            socket.close();
        }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();
    }

}

