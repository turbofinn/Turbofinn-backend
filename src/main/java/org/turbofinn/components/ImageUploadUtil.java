package org.turbofinn.components;

import com.amazonaws.AmazonServiceException;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.turbofinn.aws.AWSCredentials;

import java.io.File;

public class ImageUploadUtil {

    private static final AmazonS3 s3Client = AWSCredentials.s3Client();

    public static String uploadFile(String bucketName, String keyName, File file) {
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, keyName, file));
            return s3Client.getUrl(bucketName, keyName).toString();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return null;
        }
    }
}
