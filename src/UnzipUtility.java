import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Map;
import java.util.HashMap;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;


/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * Here it extracts the contents of a layer, splits the content into blocks and
 * takes the md5 hash value of each block
 */
public class UnzipUtility {

    private int block_size;

    private UnzipResult unzipResult;

    public UnzipUtility(int block_size) {
        this.block_size = block_size;
        this.unzipResult = new UnzipResult();
    }

    /**
     * Extracts the contents of the layer, and computes the hash value for each block in the layer.
     * @param layerName, layer to be extracted
     * @return see {@link UnzipResult}
     */
    public UnzipResult unzipLayer(String layerName) throws IOException, NoSuchAlgorithmException {
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(layerName));
        ZipEntry entry = zipIn.getNextEntry();

        String individualFiles;
        int offset;
        List<byte[]> layerContentList = new ArrayList<byte[]>();
        // iterates over entries in the zip file (here, the layer)
        while (entry != null) {
            individualFiles = entry.getName();
            /* Get Size of the file and create a byte array for the size */
            byte[] content = new byte[(int) entry.getSize()];
            offset=0;
            /* Read file from the archive into byte array */
            zipIn.read(content, offset, content.length - offset);
            layerContentList.add(content);
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }

        byte[] mergedContent = concatenateByteArrays(layerContentList);
        //Write this to some output file
        /* Only for testing
        File file = new File("original.txt");
        FileOutputStream os = new FileOutputStream(file);
        os.write(mergedContent);
        os.close();

        */
        System.out.println("Total number of bytes in the layer " + mergedContent.length);

        List<String> hashList = new ArrayList<String>();
        //Extract the hash of each block, and push the hash to byte[] blocks mapping onto S3
        String bucketName = "hashtoblocks";
        try {
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
                int i = 0;
                while(i <= mergedContent.length - this.block_size) {
                        byte[] block = Arrays.copyOfRange(mergedContent, i, i + block_size);
                        i = i + block_size;
                        MessageDigest md = MessageDigest.getInstance("SHA-1");
                        byte[] messageDigest = md.digest(block);

                        StringBuffer hexString = new StringBuffer();
                        for(int j = 0; j< messageDigest.length; j++) {
                                hexString.append(Integer.toHexString(0xFF & messageDigest[j]));
                        }
                        hashList.add(hexString.toString());
                        InputStream byteStream = new ByteArrayInputStream(block);
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentLength(block.length);
                        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, hexString.toString(), byteStream, metadata);
                        s3Client.putObject(putObjectRequest);
                }
                //For the remaining bytes
                byte[] block = Arrays.copyOfRange(mergedContent, i, mergedContent.length);
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] messageDigest = md.digest(block);

                StringBuffer hexString = new StringBuffer();
                for(int j = 0; j< messageDigest.length; j++) {
                        hexString.append(Integer.toHexString(0xFF & messageDigest[j]));
                }
                hashList.add(hexString.toString());

                InputStream byteStream = new ByteArrayInputStream(block);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(block.length);
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, hexString.toString(), byteStream, metadata);
                s3Client.putObject(putObjectRequest);
        } catch (AmazonServiceException e) {
                //Call was transmitted successfully, but S3 couldn't process it
                e.printStackTrace();
        } catch (SdkClientException e) {
                e.printStackTrace();
        }

        zipIn.close();
        this.unzipResult.setHashList(hashList);
        this.unzipResult.setHashToBlockBucketName(bucketName);
        return this.unzipResult;
    }

    public byte[] concatenateByteArrays(List<byte[]> layerContent) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for(byte[] b : layerContent) {
                os.write(b, 0, b.length);
        }
        return os.toByteArray();
    }
}
