import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;

public class VersionedBucketCreate {

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
                        // 1. Enable versioning on the bucket.
                        BucketVersioningConfiguration configuration =
                                new BucketVersioningConfiguration().withStatus("Enabled");

                        SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest =
                                        new SetBucketVersioningConfigurationRequest(bucket_name,configuration);

                        s3.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);

                        // 2. Get bucket versioning configuration information.
                        BucketVersioningConfiguration conf = s3.getBucketVersioningConfiguration(bucket_name);
                        System.out.println("bucket versioning configuration status:    " + conf.getStatus());
                } catch (AmazonS3Exception e) {
                        System.err.println(e.getErrorMessage());
                }

        }
}
