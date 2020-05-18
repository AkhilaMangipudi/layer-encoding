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

        //S3 bucketname which contains mappings from hash value of a block to the block bytes
        private String hashToBlockBucketName;

        public CompressResult() {
                this.versionId = "";
                this.encodings = new ArrayList<String>();
                this.hashToBlockBucketName = "";
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

        public void setHashToBlockBucketName(String s) {
                this.hashToBlockBucketName = s;
        }

        public String getHashToBlockBucketName() {
                return this.hashToBlockBucketName;
        }
}
