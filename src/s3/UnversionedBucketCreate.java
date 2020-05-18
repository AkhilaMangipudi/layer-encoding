import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;

public class UnversionedBucketCreate {

        public static void main(String[] args) {
                //First create an S3 client
                AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();

                //Make a create bucket request
                String bucket_name = args[0];
                System.out.println(bucket_name);

                // Create bucket
                try {
                        Bucket b = s3.createBucket(bucket_name);
                        System.out.println("Bucket successfully created");
                } catch (AmazonS3Exception e) {
                        System.err.println(e.getErrorMessage());
                }

        }
}
