import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

public class BucketCreate {
	
	public static void main(String[] args) {
		//First create an S3 client
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();

		//Make a create bucket request
		String bucket_name = "serverless685";
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
