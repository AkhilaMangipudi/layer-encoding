import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Stores the result of unzipping the layer
 */
public class UnzipResult {

        private List<String> hashList;
        private String hashToBlockBucketName;

        public UnzipResult() {
                this.hashList = new ArrayList<String>();
                this.hashToBlockBucketName = "";
        }

        public void setHashList(List<String> l) {
                this.hashList = l;
        }

        public List<String> getHashList() {
                return this.hashList;
        }

        public void setHashToBlockBucketName(String s) {
                this.hashToBlockBucketName = s;
        }

        public String getHashToBlockBucketName() {
                return this.hashToBlockBucketName;
        }
}
