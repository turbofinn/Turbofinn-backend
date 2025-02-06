package org.turbofinn.components;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import org.turbofinn.aws.AWSCredentials;
import org.turbofinn.util.Constants;

import javax.activation.MimeType;
import java.util.Calendar;

import java.util.Set;
import java.util.UUID;

public class GetPresignedUrl implements RequestHandler<GetPresignedUrl.GetPresignedUrlInput, GetPresignedUrl.GetPresignedUrlOutput> {

    public static void main(String[] args){
        GetPresignedUrlOutput output = new GetPresignedUrl().handleRequest(new GetPresignedUrlInput("PROFILE_PIC","image/jpeg"), null);
        System.out.println(output.url);
    }
    private static final AmazonS3 s3Client = AWSCredentials.s3Client();

    private static final String BUCKET_NAME = "turbo-treats";
    private static final int URL_EXPIRATION_MINUTES = 10;

    private static final Set<String> VALID_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );
    @Override
    public GetPresignedUrlOutput handleRequest(GetPresignedUrlInput input, Context context) {
        System.out.println("Received request to generate pre-signed URL.");

        try {
            // Validate input
            if (input == null && input.mimeType == null) {
                System.out.println("Invalid input: null input or MIME type received.");
                return new GetPresignedUrlOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Invalid input"), null);
            }
            if(StringUtils.isAnyBlank(input.mimeType, input.type))
                return new GetPresignedUrlOutput(new Response(400, "Invalid input"), null);


            if (!VALID_MIME_TYPES.contains(input.getMimeType())) {
                System.out.println("Invalid MIME type: " + input.getMimeType());
                return new GetPresignedUrlOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Invalid MIME type"), null);
            }


            // Generate a unique filename
            String extension = MimeTypes.getDefaultMimeTypes().forName(input.getMimeType()).getExtension();
            String fileName = UUID.randomUUID().toString() + extension;

            String folderName = "";
            switch (input.type) {
                case "PROFILE_PIC":
                    folderName="ProfileImage/";
                    break;
                case "ITEM_PIC":
                    folderName="Images/";
                    break;
                case "RESTAURANT_LOGO":
                    folderName="RestaurantLogo/";
                    break;
                default:
                    folderName="";
                    break;
            }

            // Generate pre-signed URL
            String url = generatePreSignedUrl(folderName+fileName, BUCKET_NAME, HttpMethod.PUT,input.getMimeType());

            System.out.println("Pre-signed URL generated successfully.");

            return new GetPresignedUrlOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), url);
        }catch (MimeTypeException e) {
            System.out.println("Invalid MIME type: " + input.getMimeType());
            return new GetPresignedUrlOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Invalid MIME type"), null);
        }
        catch (Exception e) {
            System.out.println("Error generating pre-signed URL: "+e);
            return new GetPresignedUrlOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Failed to generate pre-signed URL"), null);
        }
    }

    private String generatePreSignedUrl(String filePath, String bucketName, HttpMethod httpMethod,String mimeType) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, URL_EXPIRATION_MINUTES);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filePath)
                .withMethod(httpMethod)
                .withContentType(mimeType)
                .withExpiration(calendar.getTime());

        return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPresignedUrlInput {
        private String type;
        private String mimeType;

    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPresignedUrlOutput {
        private Response response;
        private String url;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private int responseCode;
        private String message;
    }
}
