# layer-encoding

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
![alt text](https://github.com/AkhilaMangipudi/layer-encoding/blob/master/serverless_uml.png?raw=true)


## Steps to run the code

