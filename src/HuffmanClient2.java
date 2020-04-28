import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

import com.amazonaws.services.s3.AmazonS3;

public class HuffmanClient2 {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
	//Send input file names, here files are equivalent to layers.
        List<String> inputList = new ArrayList<String> ();
        inputList.add(0, "input_3.txt");
        inputList.add(1, "input_4.txt");
        //inputList.add(2, "input_3.txt");
        //inputList.add(3, "input_4.txt");
        //inputList.add(4, "input_5.txt");
        //inputList.add(5, "input_6.txt");
        //inputList.add(6, "input_7.txt");
        //inputList.add(7, "input_8.txt");
	
	HuffmanDecompress2 decompressor = new HuffmanDecompress2();
        for(int i = 0; i < 3; i++){
            //establish socket connection to server
            socket = new Socket(host.getHostName(), 8080);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            
	    System.out.println("Sending request to Socket Server");
            if(i == 2)oos.writeObject("exit");
            else oos.writeObject(""+ inputList.get(i));

            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message: " + message);
	    List<String> messageList = Arrays.asList(message.split(","));
            if(!message.equals("Hi Client Exiting")) {
                decompressor.decompressFile(messageList.get(0), messageList.get(1));
            }
            //close resources
            ois.close();
            oos.close();
            Thread.sleep(100);
        }
    }
}

