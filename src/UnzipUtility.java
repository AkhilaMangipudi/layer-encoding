import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
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


/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * @author www.codejava.net
 *
 */
public class UnzipUtility {
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
	String zipFilePath = args[0];
	String destFile = args[1];

        FileWriter fw  = new FileWriter(destFile);
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();

	String individualFiles;
	int offset;
	List<byte[]> contentList = new ArrayList<byte[]>();
	int bytesTotal = 0;
        // iterates over entries in the zip file
        while (entry != null) {
	    individualFiles = entry.getName();
            /* Get Size of the file and create a byte array for the size */
            byte[] content = new byte[(int) entry.getSize()];
            offset=0;

            /* Some SOP statements to check progress */
            //System.out.println("File Name in TAR File is: " + individualFiles);
            //System.out.println("Size of the File is: " + entry.getSize());                  
            //System.out.println("Byte Array length: " + content.length);
            /* Read file from the archive into byte array */
            zipIn.read(content, offset, content.length - offset);
	    bytesTotal = bytesTotal + content.length;
	    contentList.add(content);
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
	//Now concatenate content of all lists into a single byte array
	System.out.println("total bytes are : " + bytesTotal);
	byte[] mergedContent = concatenateByteArrays(contentList);
	System.out.println("Size of the merged content is " + mergedContent.length);
	
	int block_size = 1000;
	int i = 0;
	while(i <= mergedContent.length - block_size) {
		byte[] tempArray = Arrays.copyOfRange(mergedContent, i, i + block_size);
		i = i + block_size;
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] messageDigest = md.digest(tempArray);
		
		StringBuffer hexString = new StringBuffer();
		for(int j = 0; j< messageDigest.length; j++) {
			hexString.append(Integer.toHexString(0xFF & messageDigest[j]));
		}
		fw.write(hexString.toString());
		fw.write(System.lineSeparator());
		
	}
	fw.close();
        zipIn.close();
	
    }
 
    public static byte[] concatenateByteArrays(List<byte[]> blocks) {
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	for(byte[] b : blocks) {
		os.write(b, 0, b.length);
	}
	return os.toByteArray();
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
