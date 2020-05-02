# layer-encoding
The task is to compress a 
## Dependencies
* Java 8. Please refer [Java 8 installation](https://www.javahelps.com/2015/03/install-oracle-jdk-in-ubuntu.html "Java 8 installation") for Java installation instructions.
* AWS services like S3 and DynamoDB have been used. Please download [AWS SDK for Java](https://sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip "AWS SDK for Java") which provides Java APIs for AWS services.
* [Apache Commons IO](http://www.trieuvan.com/apache//commons/io/binaries/commons-io-2.6-bin.zip "Apache Commons IO")
* [Apache Commons Compress](http://mirror.cc.columbia.edu/pub/software/apache//commons/compress/binaries/commons-compress-1.20-bin.zip "Apache Commons Compress")

## Getting started
* To connect to any of the AWS services using the SDK, you need to provide AWS credentials. Refer to https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html for setting up the AWS credentials.
* Set the Java CLASSPATH variable to run the executables without having to type the full path of the command. Following is my CLASSPATH variable. Set yours according to where you downloaded your libraries.
> export CLASSPATH=../../commons-compress-1.20/commons-compress-1.20.jar:../../commons-io-2.6/commons-io-2.6.jar:../../aws-java-sdk-1.11.762/third-party/lib/joda-time-2.8.1.jar:../../aws-java-sdk-1.11.762/third-party/lib/httpcore-4.4.11.jar:../../aws-java-sdk-1.11.762/third-party/lib/httpclient-4.5.9.jar:../../aws-java-sdk-1.11.762/third-party/lib/jackson-annotations-2.6.0.jar:../../aws-java-sdk-1.11.762/third-party/lib/jackson-core-2.6.7.jar:../../aws-java-sdk-1.11.762/third-party/lib/jackson-databind-2.6.7.3.jar:../../aws-java-sdk-1.11.762/third-party/lib/commons-logging-1.1.3.jar:../lib:/:../../aws-java-sdk-1.11.762/lib/aws-java-sdk-1.11.762.jar
## Workflow
Following dependency diagram describes the workflow for compressing a layer:

![alt text](https://github.com/AkhilaMangipudi/layer-encoding/blob/master/serverless_uml.png?raw=true)

### HuffmanServer ###
* A Http server which listens to a port and waits for clients to connect.
* Responsible for dispatching calls to HuffmanCompress application to compress the layer requested by the client.

### HuffmanClient ###
* Connects to a server and requests for a layer.
* Upon receiving the compressed layer, invokes the HuffmanDecompress application to retrieve the blocks of the layer.

### HuffmanCompress ###
* Updates the Huffman tree for every layer.
* A given layer is divided into blocks based on a pre-determined block_size and the md5 hash value of each block is computed.
* Based on the frequencies of the blocks in the layer, Huffman tree is updated and each block is encoded into a binary bit string.
* The updated Huffman tree is serialized into a string and the version is stored on Amazon S3.

### HuffmanDecompress ###
* The Huffman tree required for decoding the encoded blocks is retrieved from Amazon S3 using the versionId of the tree.
* The retrieved object from S3 is deserialized to a Huffman Tree and all the blocks in the layer are extracted.

## Steps to run the code ##
* Clone the repository using `git clone https://github.com/AkhilaMangipudi/layer-encoding.git`
* Change the directory to `layer-encoding/src`.
* All the executables are in the `layer-encoding\lib` directory. If you make changes to some file, use the following command to compile- `javac -d ../lib HuffmanServer.java`.
* Set up the AWS credentials and java CLASSPATH as described above.
* As explained in the workflow, Amazon S3 is used to store the tree objects. First create an S3 bucket which will be used later to store objects. To create a bucket, run the command `java BucketCreate <bucket-name>`. I created a bucket "serverless685" and used it for all my operations.
* Start the Huffman Server on the terminal using `java HuffmanServer`. The server will be running and be waiting for client connections.
If you run out of heap space, please run `java -Xms512m -Xmx1024m HuffmanServer`.
* Run the Huffman Client on a terminal and request for a layer using `java HuffmanClient <layer-name>`. This code assumes that the layer is a zip file present in the current directory.
* You can either delete the bucket once all the operations are over or retain it. To delete a bucket run `java BucketDelete <bucket-name>`.
#### One server - Multiple clients ####
* In a practical scenario, it is natural to have multiple clients making simultaneous requests to a server. In order to handle such a case, run `java HuffmanMultiServer` which starts a separate thread for each client. 
* For creating multiple clients, run the client code on multiple terminals and make simultaneous requests.

## Observations: Amazon S3 and Amazon DynamoDB ##
S3 and Dynamo are two major choices in the category of key-value stores. Both S3 and DynamoDB require no initialization handshakes to establish a connection, can scale on demand, and AWS charges for their actual utilization. Below are few factors that were considered while making a choice between S3 and DynamoDB:
* **Item size** - The serialized tree object is supposed to be stored and on an average, the size of this item comes to around 250 KB. This is potentially huge for Dynamo as Dynamo is preferred for storing small objects. Dynamo has a size limit of 400 KB per item, and there is a chance that our object crosses 400 KB.
* **Read time** - DynamoDB is usually faster than Amazon S3 and we can configure ProvisionedThroughput of a table in Dynamo to improve the performance. Since the object is larger than the typical objects stored in Dynamo, the read time of an object for a table with ProvisionedCapacity 25 is ~160 ms, whereas the read time from S3 is ~40 ms.
* **Pricing** - For storage, S3 is almost 10 times cheaper than that of Dynamo. For read and write access requests, keeping in mind the size of our object, the price comes out to be approximately same.
* **Consistency** - S3 provides read-after-write consistency for PUTS of new objects and eventual consistency for updates on objects. DynamoDB provides eventual consistency, but also has an option for strong read consistency. With strong read consistency enabled, the access time is expected to atleast double.
