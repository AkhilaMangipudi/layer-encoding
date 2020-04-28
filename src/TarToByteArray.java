import java.io.*;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.compress.

public class TarToByteArray {
	public static void main(String args[]) throws Exception {

		//Send the TAR file as an argument
		String tarFileName = args[0];
		/* Read TAR File into TarArchiveInputStream */
                TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(new File(tarFileName)));
                /* To read individual TAR file */
                TarArchiveEntry entry = null;
                String individualFiles;
                int offset;
                FileOutputStream outputFile=null;
                /* Create a loop to read every single entry in TAR file */
                while ((entry = myTarFile.getNextTarEntry()) != null) {
                        /* Get the name of the file */
                        individualFiles = entry.getName();
                        /* Get Size of the file and create a byte array for the size */
                        byte[] content = new byte[(int) entry.getSize()];
                        offset=0;
                        /* Some SOP statements to check progress */
                        System.out.println("File Name in TAR File is: " + individualFiles);
                        System.out.println("Size of the File is: " + entry.getSize());                  
                        System.out.println("Byte Array length: " + content.length);
                        /* Read file from the archive into byte array */
                        myTarFile.read(content, offset, content.length - offset);
                        /* Define OutputStream for writing the file */
                        outputFile=new FileOutputStream(new File(individualFiles));
                        /* Use IOUtiles to write content of byte array to physical file */
                        IOUtils.write(content,outputFile);              
                        /* Close Output Stream */
                        outputFile.close();
                }               
                /* Close TarAchiveInputStream */
                myTarFile.close();

	}
}
