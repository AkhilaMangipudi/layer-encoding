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
import java.security.NoSuchAlgorithmException;

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
 * Divides the layer into blocks, and compresses the hash value of each block using static Huffman coding.
 * Each time a new layer is requested by a client, the frequency table is updated and the Huffman tree is updated
 * with the blocks from the new layer.
 */
public class HuffmanCompress {
        //A table which maintains the frequency of each symbol in the block stream.
        private FrequencyTable freqs;

        //Blocks of freq  > thresholdFrequency are only added to the Huffman tree and encoded
        private int thresholdFrequency;

        public HuffmanCompress(int thresholdFrequency) {
                this.freqs = new FrequencyTable(new HashMap<String, Integer>());
                this.thresholdFrequency = thresholdFrequency;
        }

        /**
        * Compresses all the blocks in the given layer
        * @param layerName the layer for which the compression is requested
        * @param block_size the size of each block in the layer
        * @return See {@link CompressResult}.
        */
        public CompressResult compressLayer(String layerName, int block_size) throws IOException, NoSuchAlgorithmException {
                UnzipUtility unzipUtility = new UnzipUtility(block_size);
                CompressResult compressResult = new CompressResult();

                //First unzip the layer and get the hash values of blocks
                UnzipResult unzipResult = unzipUtility.unzipLayer(layerName);
                compressResult.setHashToBlockBucketName(unzipResult.getHashToBlockBucketName());

                //Update the frequency table with the frequencies of blocks from this layer
                updateFrequencies(unzipResult.getHashList());
                CodeTree code = this.freqs.buildCodeTree(this.thresholdFrequency);

                if (code == null) {
                        //No tree has been built because the threshold frequency is above all the frequencies
                        //in the Frequency table
                        compressResult.setVersionId("");
                        compressResult.setEncodings(unzipResult.getHashList());
                        return compressResult;
                } else {
                        List<String> encodingList = encode(code, unzipResult.getHashList());
                        //Serialize the huffman tree and store it on S3
                        String serializedTree = convert(code.root);

                        //Now store this string onto S3 bucket serverless685
                        String objKeyName = "huffman";
                        String objVersion = "";
                        try {
                                AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
                                PutObjectResult putResult = s3Client.putObject("serverless685", objKeyName, serializedTree);
                                objVersion = putResult.getVersionId();
                                System.out.println("Version number of the object is "  + putResult.getVersionId());
                        } catch (AmazonServiceException e) {
                                //Call was transmitted successfully, but S3 couldn't process it
                                e.printStackTrace();
                        } catch (SdkClientException e) {
                                e.printStackTrace();
                        }
                        compressResult.setVersionId(objVersion);
                        compressResult.setEncodings(encodingList);
                        return compressResult;
                }
        }

        /**
        * Converts the tree into a string using pre-order traversal
        * @param node The root of the tree
        * @return String the serialized version of the tree.
        */
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

        /**
        * Helper function for serializing the huffman tree.
        */
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

        /**
        * Updates the frequencies of the blocks in {@Link FrequencyTable}
        * @param hashList the list of hash values of blocks in the current layer
        */
        private void updateFrequencies(List<String> hashList) {
                for(String hash : hashList) {
                        this.freqs.increment(hash);
                }
        }

        /**
        * Encodes each block into a string of 1s and 0s using the Huffman tree
        * @param codeTree the Huffman tree built
        * @param hashList the list of hash values of blocks
        * @return list of encodings of blocks in the layer.
        */
        public List<String> encode(CodeTree codeTree, List<String> hashList) throws IOException {
                List<String> encodingList = new ArrayList<String>();
                for(String hash : hashList) {
                        //Only some blocks have encoding, if they don't have an encoding, just send the hash directly
                        if ("#".equals(codeTree.getCode(hash))) {
                                encodingList.add(hash);
                        }
                        else {
                                encodingList.add(codeTree.getCode(hash));
                        }
                }
                return encodingList;
        }
}
