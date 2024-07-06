package org.turbofinn.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.applicationautoscaling.AWSApplicationAutoScaling;
import com.amazonaws.services.applicationautoscaling.AWSApplicationAutoScalingClientBuilder;
import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.AmazonAthenaClientBuilder;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.glue.AWSGlue;
import com.amazonaws.services.glue.AWSGlueClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.turbofinn.util.Constants;

import java.time.Instant;
import java.util.UUID;

public class AWSCredentials {

    public static Regions AWSRegions = Regions.US_EAST_1;
    public static boolean useLocalCredentials = Constants.USE_LOCAL_AWS_CREDENTIALS;

    public static DynamoDBMapper dynamoDBMapper() {
        return new AWSCredentials()._dynamoDBMapper();
    }

    private DynamoDBMapper _dynamoDBMapper() {
        var mapper = cachedDynamoDBMapper.getDynamoDBMapper();
        if (mapper != null) { return mapper; }
        synchronized (this) {
            mapper = cachedDynamoDBMapper.getDynamoDBMapper();
            if (mapper != null) { return mapper; }
            mapper = new DynamoDBMapper(awsClient());
            cachedDynamoDBMapper.setDynamoDBMapper(mapper);
            return mapper;
        }
    }

    private final CachedDynamoDbMapper cachedDynamoDBMapper = new CachedDynamoDbMapper();
    private static class CachedDynamoDbMapper {
        private DynamoDBMapper dynamoDBMapper;
        private Instant createdAt = Instant.EPOCH;
        private static final long ttl = 15 * 60;	//	15 minutes

        DynamoDBMapper getDynamoDBMapper() {
            return dynamoDBMapper != null && createdAt.plusSeconds(ttl).isAfter(Instant.now()) ? dynamoDBMapper : null;
        }

        void setDynamoDBMapper(DynamoDBMapper mapper) {
            this.dynamoDBMapper = mapper;
            createdAt = Instant.now();
        }
    }

    public static DynamoDBMapper consistentReadDynamoDBMapper() {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build();
        return new DynamoDBMapper(awsClient(), mapperConfig);
    }

    public static DynamoDB dynamoDB() {
        return new DynamoDB(awsClient());
    }

    private static AWSCredentialsProvider cachedAWSStaticCredentialsProvider;
    private static String localProfileName = Constants.LOCAL_AWS_CREDENTIALS_PROFILE;
    public static AWSCredentialsProvider getAWSCredentialProvider() {
        if (cachedAWSStaticCredentialsProvider == null) {
            cachedAWSStaticCredentialsProvider = useLocalCredentials ?
                    new AWSStaticCredentialsProvider(new ProfileCredentialsProvider(localProfileName).getCredentials()) :
                    new EnvironmentVariableCredentialsProvider();
        }
        return cachedAWSStaticCredentialsProvider;
    }

    public static AmazonDynamoDB awsClient() {
        return AmazonDynamoDBClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(AWSRegions).build();
    }

    public static AmazonCognitoIdentity cognitoIdentityClient() {
        return AmazonCognitoIdentityClientBuilder.standard().withCredentials(getAWSCredentialProvider()).build();
    }

    public static AmazonSNS snsClient() {
        return AmazonSNSClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(AWSRegions).build();
    }

    public static AmazonS3 s3Client() {
        return AmazonS3ClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(AWSRegions).build();
    }

    public static AmazonS3 s3Client(Regions region) {
        return AmazonS3ClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(region).build();
    }

    public static AmazonSimpleEmailService sesClient() {
        return AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(AWSRegions).build();
    }

    public static AWSLambda lambdaClient() {
        return AWSLambdaClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(AWSRegions).build();
    }
    public static AWSApplicationAutoScaling aaClient() {
        return AWSApplicationAutoScalingClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(AWSRegions).build();
    }
    public static AmazonAthena athenaClient() {
        return AmazonAthenaClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(AWSRegions).build();
    }
    public static AmazonSQS sqsClient() {
        return AmazonSQSClientBuilder.standard().withCredentials(getAWSCredentialProvider()).withRegion(AWSRegions).build();
    }

    public static AWSGlue GlueClient() {
        return AWSGlueClientBuilder.standard().withCredentials(new EnvironmentVariableCredentialsProvider()).withRegion(AWSRegions).build();
    }

    public static AWSLambda lambdaClient(String localProfileName) {
        return AWSLambdaClientBuilder.standard().withCredentials(new ProfileCredentialsProvider(localProfileName)).withRegion(AWSRegions).build();
    }


    private static final String testAwsRequestId = UUID.randomUUID().toString();


}