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
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.*;


import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TFLambdaUpdate {

    private static String awsCredentialsProfileName = Constants.IS_PROD ? "turbofinn" : "turbofinn";
    private static String lambdaLayer = Constants.IS_PROD ? "arn:aws:lambda:us-east-1:058264291984:layer:TF-Project-Layer:1" : "arn:aws:lambda:us-east-1:058264291984:layer:TF-Project-Layer:1";

    private static String projectPOMArtifactId = "Turbofinn-backend";
    private static String projectPOMVersion = "1.0-SNAPSHOT";
    private static String projectPOMPackaging = "jar";
    private static String mavenPackagedZipFileName = projectPOMArtifactId + "-" + projectPOMVersion + "." + projectPOMPackaging;
    private static String mavenPackagedZipFilePath = "target/" + mavenPackagedZipFileName;
    private static final boolean updateLayer = false;
    private static String s3BucketName = Constants.IS_PROD ? "lambda-layer-turbofinn" : "lambda-layer-turbofinn";
    private static String lambdaCodeZipFileName = Instant.now() + ".zip";
    private static List<String> aliasNameAndroid = Constants.IS_PROD ? Arrays.asList("") : Arrays.asList("");
    private static Regions region = Constants.IS_PROD ? Regions.US_EAST_1 : Regions.US_EAST_1;


    private static List<String> lambdaFunctionsToupload =   List.of("HelloWorld");


    public static void main(String[] args) throws MavenInvocationException {
        if (Constants.USE_LOCAL_AWS_CREDENTIALS)
            System.out.println("Please change USE_LOCAL_AWS_CREDENTIALS");
        else {
            if (Constants.IS_PROD) {
                System.out.println("Do you want to upload for prod : enter Yes or no");
                Scanner scanner = new Scanner(System.in);
                String string = scanner.nextLine();
                scanner.close();
                if (!(string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("y")))
                    System.exit(0);
            }

            createLambdaCodeZipFile();
            System.out.println(String.format("***************************** Environment ===> %s ********************************", Constants.IS_PROD ? "PROD" : "DEV"));
            uploadZipFileToS3(lambdaCodeZipFileName, mavenPackagedZipFilePath);
            parallelUpdateCodeAndAlias();
//            updateCodeAndAlias();
        }
    }

    private static void createLambdaCodeZipFile() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setJavaHome(new File("/Users/gauravsingh/Library/Java/JavaVirtualMachines/corretto-21.0.3/Contents/Home"));
        request.setGoals(Collections.singletonList("package"));
        request.setBaseDirectory(new File(System.getProperty("user.dir")));
        DefaultInvoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("HOME") + "/Applications/apache-maven-3.9.6/"));
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Could not create lambda package file.");
        }
    }

    private static void uploadZipFileToS3(String fileName, String mvnRelativePath) {
        System.out.println("Uploading " + fileName);
        AmazonS3 awsLambdaClient = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new ProfileCredentialsProvider(awsCredentialsProfileName).getCredentials()))
                .build();
        File file = new File(System.getProperty("home.dir"), mvnRelativePath);
        String fileSize = FileUtils.byteCountToDisplaySize(file.length());
        System.out.println("Uploading File: " + fileName);
        System.out.println("Upload size: " + fileSize);
        Instant uploadStartInstant = Instant.now();
        awsLambdaClient.putObject(s3BucketName, fileName, file);
        long timeInSecs = uploadStartInstant.until(Instant.now(), ChronoUnit.SECONDS);
//        long speedInBps = file.length() / timeInSecs;
//        System.out.println("Uploaded file in " + timeInSecs + " secs at a speed of " + FileUtils.byteCountToDisplaySize(speedInBps) + "ps");
    }

    private static void parallelUpdateCodeAndAlias() {
        System.out.println("Lambda Functions To Update: " + lambdaFunctionsToupload.size());

        ExecutorService service = Executors.newFixedThreadPool(4);
        Lists.partition(lambdaFunctionsToupload.stream().collect(Collectors.toList()), 12)
                .forEach(list ->
                        service.submit(() ->
                                updateCodeAndAlias(list))
                );
        service.shutdown();
        try {
            service.awaitTermination(100, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateCodeAndAlias(List<String> lambdaFunctionsTouploadList) {
        AtomicInteger i= new AtomicInteger(1);
        AWSLambda awsLambdaClient = AWSLambdaClientBuilder.standard().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(new ProfileCredentialsProvider(awsCredentialsProfileName).getCredentials())).build();
        lambdaFunctionsTouploadList.parallelStream().forEach(lambdaFunction -> {
            System.out.println("----------------------------------------");
            System.out.println("Uploading "+ (lambdaFunctionsToupload.indexOf(lambdaFunction)+1) +" of "+ lambdaFunctionsToupload.size());
            System.out.print("Lambda function '" + lambdaFunction + "' ");
            try {

                UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = new UpdateFunctionConfigurationRequest().withFunctionName(lambdaFunction).withRuntime(Runtime.Java21);
                UpdateFunctionConfigurationResult updateFunctionConfigurationResult = awsLambdaClient.updateFunctionConfiguration(updateFunctionConfigurationRequest);
                System.out.println("Added Layer " + updateFunctionConfigurationResult.getLayers().toString());
                waitUntilActive(awsLambdaClient, lambdaFunction);

                UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest().withFunctionName(lambdaFunction).withPublish(true).withS3Bucket(s3BucketName).withS3Key(lambdaCodeZipFileName);
                UpdateFunctionCodeResult updateCodeResult = awsLambdaClient.updateFunctionCode(updateFunctionCodeRequest);
                System.out.println("Code updated with version : " + updateCodeResult.getVersion() + ", Handler : " + updateCodeResult.getHandler());
                waitUntilActive(awsLambdaClient, lambdaFunction);

                UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest1 = new UpdateFunctionConfigurationRequest().withFunctionName(lambdaFunction).withLayers(lambdaLayer);
                UpdateFunctionConfigurationResult updateFunctionConfigurationResult1 = awsLambdaClient.updateFunctionConfiguration(updateFunctionConfigurationRequest1);
                System.out.println("Added Layer " + updateFunctionConfigurationResult1.getLayers().toString());

//                for (String alias : aliasNameAndroid) {
//                    try {
//                        UpdateAliasRequest updateAliasRequest = new UpdateAliasRequest().withFunctionName(lambdaFunction).withFunctionVersion(updateCodeResult.getVersion()).withName(alias);
//                        awsLambdaClient.updateAlias(updateAliasRequest);
//                        System.out.println("Updated alias " + alias + ", Version: " + updateCodeResult.getVersion());
//                    } catch (ResourceNotFoundException e) {
//                        CreateAliasRequest createAliasRequest = new CreateAliasRequest().withFunctionName(lambdaFunction).withFunctionVersion(updateCodeResult.getVersion()).withName(alias);
//                        awsLambdaClient.createAlias(createAliasRequest);
//                        System.out.println("Created alias " + alias + ", Version: " + updateCodeResult.getVersion());
//                    }
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("----------------------------------------");
        });

    }

    private static boolean isFunctionActive(AWSLambda lambdaClient, String functionName) {
        FunctionConfiguration lambdaConfig = lambdaClient.getFunction(new GetFunctionRequest().withFunctionName(functionName))
                .getConfiguration();
        return lambdaConfig.getState().equals("Active") && lambdaConfig.getLastUpdateStatus().equals("Successful");
    }

    public static void waitUntilActive(AWSLambda lambdaClient, String functionName) {
        boolean isActive  = false;
        int hardLimit = 4;
        int attempts  = 0;
        do {
            if (attempts++ >= hardLimit) return;
            int waitTime = getWaitTimeExp(attempts);
            System.out.print("\nWaiting: " + (waitTime/1000) + " seconds : " + functionName);
            waitForSec(waitTime);
            isActive = isFunctionActive(lambdaClient, functionName);
        } while (!isActive);
    }

    private static int getWaitTimeExp(int attempts) {
        return (int) (Math.pow( 2, attempts) * 1000);
    }

    private static void waitForSec(Integer milliSeconds) {
        int timeInMilliSecond = milliSeconds == null ? 1000 : milliSeconds;
        try { Thread.sleep(timeInMilliSecond); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }



}