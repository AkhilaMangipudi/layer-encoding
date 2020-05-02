import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Stores the result of unzipping the layer
 */
public class UnzipResult {

	private List<String> hashList;
	private Map<String, byte[]> hashToBlockBytesMap;

	public UnzipResult() {
		this.hashList = new ArrayList<String>();
		this.hashToBlockBytesMap = new HashMap<String, byte[]>();
	}

	public void setHashList(List<String> l) {
		this.hashList = l;
	}

	public List<String> getHashList() {
		return this.hashList;
	}

	public void setHashToBlockBytesMap(Map<String, byte[]> m) {
		this.hashToBlockBytesMap = m;
	}

	public Map<String, byte[]> getHashToBlockBytesMap() {
		return this.hashToBlockBytesMap;
	}
}
