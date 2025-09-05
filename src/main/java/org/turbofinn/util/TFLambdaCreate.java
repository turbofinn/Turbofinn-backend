package org.turbofinn.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.Runtime;
import com.amazonaws.services.lambda.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.*;
import org.turbofinn.DynamoToRDS.DynamoDBToRDSHandler;
import org.turbofinn.components.*;
import org.turbofinn.razorPayUtil.CreatePaymentOrder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class TFLambdaCreate {
    private static String awsCredentialsProfileName = Constants.IS_PROD ? "Algoflow" : "Algoflow";
    private static String lambdaLayer = Constants.IS_PROD ? "arn:aws:lambda:us-east-1:676373376148:layer:TF-Project-Layer:1" : "arn:aws:lambda:us-east-1:676373376148:layer:TF-Project-Layer:1";
    private static String projectPOMArtifactId = "Turbofinn-backend";
    private static String projectPOMVersion = "1.0-SNAPSHOT";
    private static String projectPOMPackaging = "jar";
    private static String mavenPackagedZipFileName = projectPOMArtifactId + "-" + projectPOMVersion + "." + projectPOMPackaging;
    private static String mavenPackagedZipFilePath = "target/" + mavenPackagedZipFileName;
    private static int memorySize = 3008;  // in MB
    private static int timeOut = 900; // in seconds

    private static List<Class<?>> lambdaFunctionsToupload = Arrays.asList(DynamoDBToRDSHandler.class, CreatePaymentOrder.class);
    private static String s3BucketName = Constants.IS_PROD ? "lambda-layer-algoflow" : "lambda-layer-algoflow";
    private static String lambdaCodeZipFileName = Constants.IS_PROD ? Instant.now() + ".zip" : Instant.now() + ".zip";
    private static List<String> aliasNameAndroid = Constants.IS_PROD ? Arrays.asList("dsa-dashboard-v2") : Arrays.asList("Retailer-Android-1-4");
    private static String lambdaRoleArn = Constants.IS_PROD ? "arn:aws:iam::676373376148:role/lambda-basic-execution" : "arn:aws:iam::676373376148:role/lambda-basic-execution";
    private static Regions region = Constants.IS_PROD ? Regions.US_EAST_1 : Regions.US_EAST_1;

    public static void main(String[] args) throws IOException, InterruptedException, MavenInvocationException {
        createLambdaCodeZipFile();
        System.out.println(String.format("***************************** Environment ===> %s ********************************", Constants.IS_PROD ? "PROD" : "DEV"));
        uploadZipFileToS3(lambdaCodeZipFileName, mavenPackagedZipFilePath);
        createLambdaAndUpdateAlias();
    }

    private static void createLambdaCodeZipFile() throws IOException, InterruptedException, MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals(Arrays.asList("package"));
        request.setBaseDirectory(new File(System.getProperty("user.dir")));
        DefaultInvoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File("C:\\Program Files\\apache-maven-3.9.10"));
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Could not create lambda package file.");
        }
    }

    private static void uploadZipFileToS3(String fileName, String mvnRelativePath) throws FileNotFoundException {
        System.out.println("Uploading " + fileName);
        AmazonS3 awsLambdaClient = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new ProfileCredentialsProvider(awsCredentialsProfileName).getCredentials()))
                .build();
        File file = new File(System.getProperty("home.dir"), mvnRelativePath);
        String fileSize = FileUtils.byteCountToDisplaySize(file.length());
        System.out.println("Uploading File: "+fileName);
        System.out.println("Upload size: " + fileSize);
        Instant uploadStartInstant = Instant.now();
        awsLambdaClient.putObject(s3BucketName, fileName, file);
        long timeInSecs = uploadStartInstant.until(Instant.now(), ChronoUnit.SECONDS);
        long speedInBps = file.length()/timeInSecs;
        System.out.println("Uploaded file in " + timeInSecs + " secs at a speed of "+FileUtils.byteCountToDisplaySize(speedInBps)+"ps");
    }

    private static void createLambdaAndUpdateAlias() {
        AWSLambda awsLambdaClient = AWSLambdaClientBuilder.standard().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(new ProfileCredentialsProvider(awsCredentialsProfileName).getCredentials())).build();
        System.out.println("Lambda Functions To Update: " + lambdaFunctionsToupload.size());
        lambdaFunctionsToupload.forEach(lambdaFunction -> {
            System.out.println("---------------------------------------- "+lambdaFunction.getSimpleName()+" ----------------------------------------");
            try {

                CreateFunctionRequest createFunctionRequest = new CreateFunctionRequest().withRuntime(Runtime.Java21)
                        .withFunctionName(lambdaFunction.getSimpleName())
                        .withCode(new FunctionCode().withS3Bucket(s3BucketName).withS3Key(lambdaCodeZipFileName))
                        .withHandler(lambdaFunction.getName()).withLayers(lambdaLayer).withMemorySize(memorySize).withPublish(true)
                        .withRole(lambdaRoleArn).withTimeout(timeOut);
                CreateFunctionResult createFunctionResult = awsLambdaClient.createFunction(createFunctionRequest);
                System.out.println(": Created Lambda Successfully.");
                for (String alias : aliasNameAndroid) {
                    try {
                        UpdateAliasRequest updateAliasRequest = new UpdateAliasRequest().withFunctionName(lambdaFunction.getSimpleName()).withFunctionVersion(createFunctionResult.getVersion()).withName(alias);
                        awsLambdaClient.updateAlias(updateAliasRequest);
                        System.out.println("Updated alias " + alias + ", Version: " + createFunctionResult.getVersion());
                    } catch (ResourceNotFoundException e) {
                        CreateAliasRequest createAliasRequest = new CreateAliasRequest().withFunctionName(lambdaFunction.getSimpleName()).withFunctionVersion(createFunctionResult.getVersion()).withName(alias);
                        awsLambdaClient.createAlias(createAliasRequest);
                        System.out.println("Created alias " + alias + ", Version: " + createFunctionResult.getVersion());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("--------------------------------------------------------------------------------");
        });

    }
}
