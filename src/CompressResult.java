import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Result class for storing the result of Huffman Compression.
 */

public class CompressResult {

	//VersionId of the tree stored in Amazon S3
	private String versionId;
	
	//Encodings of the blocks present in the layer
	private List<String> encodings;

	//Mapping from hash value of a block to its byte content.
	private Map<String, byte[]> hashToBlockMap;

	public CompressResult() {
		this.versionId = "";
		this.encodings = new ArrayList<String>();
		this.hashToBlockMap = new HashMap<String, byte[]>();
	}

	public void setVersionId(String v) {
		this.versionId = v;	
	}

	public String getVersionId() {
		return this.versionId;
	}

	public void setEncodings(List<String> l) {
		this.encodings = l;
	}

	public List<String> getEncodings() {
		return this.encodings;
	}
		
	public void setHashToBlockMap(Map<String, byte[]> m) {
		this.hashToBlockMap = m;
	}

	public Map<String, byte[]> getHashToBlockMap() {
		return this.hashToBlockMap;
	}
}
