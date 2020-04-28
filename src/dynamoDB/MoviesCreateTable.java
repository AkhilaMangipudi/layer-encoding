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

public class MoviesCreateTable {

    public static void main(String[] args) throws Exception {

        //AwsSessionCredentials awsCreds = AwsSessionCredentials.create("AKIARVYGEBL4W6H5OE6F",
        //                                                            "3/qlp/qE1+cquBbGgVwcFuWoYGDL60ILc10Xxgx+");
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            //.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration( "us-east-2"))
            //.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .withRegion("us-east-2")
            .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "encodings";

        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(tableName,
                Arrays.asList(new KeySchemaElement("version_num", KeyType.HASH)), // Sort key
                Arrays.asList(new AttributeDefinition("version_num", ScalarAttributeType.N)),
                new ProvisionedThroughput(25L, 25L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }

    }
}

