import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

/**
 * Sample class used for simple put and get operations onto dynamoDB tables.
 * Usage: java Encodings <table-name> <mode> <key>
 */
public class Encodings {

    public static void main(String[] args) throws Exception {
	String tableName = args[0];
        String mode = args[1];
        String path = args[2];

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion("us-east-2")
            .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable(tableName);

        if(mode.equals("create")) {
                char[] chars = new char[217749];
                Arrays.fill(chars, 'f');
                String stringData = new String(chars);
                byte[] data = stringData.getBytes();
                try {
                        System.out.println("Adding a new item...");
                        long startTime = System.nanoTime();
                        PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey("version_num", Integer.parseInt(path)).withBinarySet("encoding", data));
                        long timeElapsed = System.nanoTime() - startTime;
                        System.out.println("Time to put an item" + timeElapsed / (1000000));
                } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.err.println("Unable to add item");
                }

        }
        else if(mode.equals("get")) {
                GetItemSpec spec = new GetItemSpec().withPrimaryKey("version_num", Integer.parseInt(path));
                try {
                        double temp = 0.0;
                        int count = 100;
                        int i = 0;
                        long startTime = 0;
                        long timeElapsed = 0;
                        for(i = 0; i < count; i++) {
                                startTime = System.nanoTime();
                                Item outcome = table.getItem(spec);
                                timeElapsed = System.nanoTime() - startTime;
                                temp = temp + timeElapsed;
                        }
                        System.out.println("Time taken to read data is " + temp /(count * 1000000) + " ms");
                } catch (Exception e) {
                        System.err.println("Unable to retrieve item");
                        System.err.println(e.getMessage());
                }
        }

    }
}

