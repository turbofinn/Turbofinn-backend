package org.turbofinn.components;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.turbofinn.aws.AWSCredentials;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUploadUtil {

    private static final AmazonS3 s3Client = AWSCredentials.s3Client();

    public static String uploadFile(String bucketName, String keyName, File file) {
        try {
            // Resize the image
            BufferedImage originalImage = ImageIO.read(file);
            BufferedImage resizedImage = resizeImage(originalImage, 200, 200); // Change size as needed

            // Convert resized image to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            byte[] bytes = baos.toByteArray();

            // Upload the resized image to S3
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            s3Client.putObject(new PutObjectRequest(bucketName, keyName, bis, null));

            return s3Client.getUrl(bucketName, keyName).toString();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Error processing the image file: " + e.getMessage());
            return null;
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }
}
