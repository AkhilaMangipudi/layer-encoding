/*
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 *
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

/**
 * Compression application using static Huffman coding.
 * <p>Usage: java HuffmanCompress InputFile OutputFile</p>
 * <p>Then use the corresponding "HuffmanDecompress" application to recreate the original input file.</p>
 * <p>Note that the application uses an alphabet of 257 symbols - 256 symbols for the byte values
 * and 1 symbol for the EOF marker. The compressed file format starts with a list of 257
 * code lengths, treated as a canonical code, and then followed by the Huffman-coded data.</p>
 */
public class HuffmanCompress {

        private FrequencyTable freqs;

	public HuffmanCompress() {
		this.freqs = new FrequencyTable(new HashMap<String, Integer>());
	}
	// Command line main application function.
        public List<String> compressFile(String fileName) throws IOException {
		List<String> resultList = new ArrayList<String>();
		String[] parts = fileName.split("_");
		String outputFileName = "layer_c_" + parts[1];

	 	//Use the input to fetch the layer needed

                FileReader in = new FileReader(fileName);
                FileWriter fw = new FileWriter(outputFileName);

                // Read input file once to compute symbol frequencies.
                // The resulting generated code is optimal for static Huffman coding and also canonical.
                updateFrequencies(in);
                //this.freqs.increment(256);  // EOF symbol gets a frequency of 1

		//Time taken to build new tree with updated encodings
		/*
		CodeTree code = null;
		double time = 0.0;
		int count = 1000;
		long startTime = 0;
		long timeElapsed = 0;
		for(int i = 0; i < count; i++) {
			startTime = System.nanoTime();
			code = this.freqs.buildCodeTree();
			timeElapsed = System.nanoTime() - startTime;
			time = time + timeElapsed;
		}
		System.out.println("Time taken for buildCodeTree is " + time / (count * 1000000) + "ms");
		*/
                CodeTree code = this.freqs.buildCodeTree();
		in.close();

		compress(code, fileName, fw);
		//By this time, the whole file has been compressed and written to a new output file
		//We need to serialize the tree into a string and store it as a string onto S3
		//

		/*
		double time = 0.0;
                int count = 1000;
                long startTime = 0;
                long timeElapsed = 0;
		String serializedTree = "";
                for(int i = 0; i < count; i++) {
                        startTime = System.nanoTime();
                        serializedTree = convert(code.root); 
                        timeElapsed = System.nanoTime() - startTime;
                        time = time + timeElapsed;
                }
                System.out.println("Time taken for serializing the tree is " + time / (count * 1000000) + "ms");
		*/
		String serializedTree = convert(code.root);
		System.out.println("SerializedTree length is " + serializedTree.length());
		//System.out.println("serializedTree is " + serializedTree);
		
		//Now store this string onto S3 bucket serverless685
		String objKeyName = "huffman";
		String objVersion = "";
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
			/*
			double time = 0.0;
	                int count = 1000;
        	        long startTime = 0;
                	long timeElapsed = 0;
                	for(int i = 0; i < count; i++) {
                        	startTime = System.nanoTime();
                        	PutObjectResult putResult = s3Client.putObject("serverless685", objKeyName, serializedTree);
                        	timeElapsed = System.nanoTime() - startTime;
                        	time = time + timeElapsed;
                	}
			System.out.println("Time taken for putObject into S3 is " + (time) / (count * 1000000) + " ms");
			*/
			PutObjectResult putResult = s3Client.putObject("serverless685", objKeyName, serializedTree);
			objVersion = putResult.getVersionId();
			System.out.println("Version number of the object is "  + putResult.getVersionId());
		} catch (AmazonServiceException e) {
			//Call was transmitted successfully, but S3 couldn't process it
			e.printStackTrace();
		} catch (SdkClientException e) {
			e.printStackTrace();
		}
		resultList.add(0, outputFileName);
		resultList.add(1, objVersion);
		return resultList;
        }

	public String convert(Node node) {
		String result = "";
		try {
                        if(node != null) {
                                result = serial(new StringBuilder(), node).toString();
                        }
                } catch(Exception e) {
                        e.printStackTrace();
                }
                return result;
	}

	private StringBuilder serial(StringBuilder str, Node root) {
                if(root == null) return str.append("#");
		if(root instanceof InternalNode) {
			InternalNode node = (InternalNode)root;
			str.append("int").append(",");
			serial(str, node.leftChild).append(",");
			serial(str, node.rightChild);
		}
		else if(root instanceof Leaf) {
			Leaf leaf = (Leaf)root;
			str.append(leaf.symbol);
		}
		else {
			System.out.println("Unexpected type of node in the tree");
		}
                return str;
        }
        // Returns a frequency table based on the bytes in the given file.
        // Also contains an extra entry for symbol 256, whose frequency is set to 0.
        private void updateFrequencies(FileReader in) throws IOException {
                try (BufferedReader br = new BufferedReader(in)) {
			String readLine = null;
                        while ((readLine = br.readLine()) != null) {
				this.freqs.increment(readLine);
                        }
                }
        }


	//This is my compress function
	static void compress(CodeTree code, String fileName, FileWriter fw) throws IOException {
		HuffmanEncoder enc = new HuffmanEncoder(fw);
		enc.codeTree = code;
		String readLine = null;
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			while ((readLine = br.readLine()) != null) {
				enc.write(readLine);
			}	
		//enc.write(256); //EOF
		}
		fw.close();
	}

}

