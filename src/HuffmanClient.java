import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import com.amazonaws.services.s3.AmazonS3;

/**
 * Class representing the client, The client asks for a specific layer to the registry(Server).
 * Upon receiving the huffman encodings of all the blocks in the layer, client reconstructs the layer
 */
public class HuffmanClient {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, use the server IP address
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        HuffmanDecompress huffmanDecompress = new HuffmanDecompress();
        for(int i = 0; i < 2; i++){
            //establish socket connection to server
            socket = new Socket(host.getHostName(), 8080);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());

            if(i == 1)oos.writeObject("exit");
            else oos.writeObject(args[0]);

            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            if(!message.equals("Hi Client Exiting")) {
                String versionId = message;
                List<String> encodingsList = (List<String>) ois.readObject();
                String hashToBlockBucketName = (String) ois.readObject();
                byte[] layerContent = huffmanDecompress.decompressLayer(versionId, encodingsList, hashToBlockBucketName);

                /*
                //Code for testing
                File file = new File("recovered.txt");
                FileOutputStream os = new FileOutputStream(file);
                os.write(layerContent);
                os.close();
                */
            }
            //close resources
            ois.close();
            oos.close();
            Thread.sleep(100);
        }
    }
}
