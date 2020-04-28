import java.util.Arrays;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.regions.Regions;

public class MoviesDeleteTable {

    public static void main(String[] args) throws Exception {

        //AwsSessionCredentials awsCreds = AwsSessionCredentials.create("AKIARVYGEBL4W6H5OE6F",
        //                                                            "3/qlp/qE1+cquBbGgVwcFuWoYGDL60ILc10Xxgx+");
        //AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        //    .withRegion(Region.US_EAST_2);
        //    .build();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                                .withRegion("us-east-2")
                                .build();
        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "encodings";
        Table table = dynamoDB.getTable(tableName);
        try {
            System.out.println("Attempting to delete table; please wait...");
            table.delete();
            table.waitForDelete();
            System.out.print("Success.");

        }
        catch (Exception e) {
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }
    }
}

