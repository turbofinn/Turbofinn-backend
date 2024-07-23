package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Offers;
import org.turbofinn.util.Constants;

import java.io.File;
import java.util.UUID;


public class OfferDetail implements RequestHandler<OfferDetail.OfferInput, OfferDetail.OfferOutput> {

    public static void main(String []args){
        OfferInput idInput = new OfferInput();
        idInput.setOfferId("567");
        idInput.setOfferName("Prime");
        idInput.setDetails("Mega Offer");
        idInput.setStartDate("24/07/2024");
        idInput.setEndDate("28/07/2024");
        idInput.setRestaurantId("1");
        idInput.setReferenceId("tegw");
        idInput.setImage("./mega.png");
        idInput.setAction("CREATE");
        System.out.println(new Gson().toJson(new OfferDetail().handleRequest(idInput, null)));
    }

    @Override
    public OfferDetail.OfferOutput handleRequest(OfferDetail.OfferInput input, Context context) {
        if (input == null) {
            return new OfferDetail.OfferOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        switch (DB_Offers.ActionType.getActionType(input.action)) {
            case CREATE:
                return createNewOffer(input);
            case UPDATE:
                return updateOffer(input);
            case DELETE:
                return softDeleteOffer(input);
            case FETCH:
                return fetchOfferById(input);
            default:
                return new OfferDetail.OfferOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
    }

    public OfferDetail.OfferOutput createNewOffer(OfferDetail.OfferInput input){

        //logic for uploading image in s3
        String bucketName = "turbo-treats";
        String uuid = UUID.randomUUID().toString();
        String imageKey = "OfferImages/" + uuid + ".jpg";
        File imageFile = new File(input.getImage());

        String imageUrl = ImageUploadUtil.uploadFile(bucketName, imageKey, imageFile);
        if (imageUrl == null) {
            return new OfferDetail.OfferOutput(new OfferDetail.Response(Constants.GENERIC_RESPONSE_CODE, "Failed to upload image to S3"), null);
        }

        DB_Offers dbOffer = new DB_Offers();
        dbOffer.setOfferId(input.getOfferId());
        dbOffer.setOfferName(input.getOfferName());
        dbOffer.setDetails(input.getDetails());
        dbOffer.setStartDate(input.getStartDate());
        dbOffer.setEndDate(input.getEndDate());
        dbOffer.setReferenceId(input.getReferenceId());
        dbOffer.setRestaurantId(input.getRestaurantId());
        dbOffer.setImage(input.getImage());
        dbOffer.save();
        return new OfferDetail.OfferOutput(new OfferDetail.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), dbOffer);
    }

    public OfferDetail.OfferOutput updateOffer(OfferDetail.OfferInput input){

        //logic for uploading image in s3
        String bucketName = "turbo-treats";
        String uuid = UUID.randomUUID().toString();
        String imageKey = "OfferImages/" + uuid + ".jpg";
        File imageFile = new File(input.getImage());

        String imageUrl = ImageUploadUtil.uploadFile(bucketName, imageKey, imageFile);
        if (imageUrl == null) {
            return new OfferDetail.OfferOutput(new OfferDetail.Response(Constants.GENERIC_RESPONSE_CODE, "Failed to upload image to S3"), null);
        }

        DB_Offers dbOffer = DB_Offers.fetchOfferByID(input.offerId);
        dbOffer.setOfferName(input.getOfferName());
        dbOffer.setDetails(input.getDetails());
        dbOffer.setStartDate(input.getStartDate());
        dbOffer.setEndDate(input.getEndDate());
        dbOffer.setReferenceId(input.getReferenceId());
        dbOffer.setRestaurantId(input.getRestaurantId());
        dbOffer.setImage(input.getImage());
        dbOffer.save();
        return new OfferDetail.OfferOutput(new OfferDetail.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), dbOffer);
    }

    private OfferDetail.OfferOutput softDeleteOffer(OfferDetail.OfferInput input) {
        if(input.getOfferId()==null){
            return new OfferDetail.OfferOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Offer id is null"), null);
        }
        DB_Offers dbOffer = DB_Offers.fetchOfferByID(input.offerId);
        dbOffer.setIsDeleted("true");
        dbOffer.save();
        return new OfferDetail.OfferOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), null);
    }

    private OfferDetail.OfferOutput fetchOfferById(OfferDetail.OfferInput input) {
        if (input.getOfferId() == null) {
            return new OfferDetail.OfferOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        DB_Offers offer = DB_Offers.fetchOfferByID(input.getOfferId());

        if (offer == null) {
            return new OfferDetail.OfferOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Item not found"), null);
        }
        return new OfferDetail.OfferOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), offer);
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class OfferInput {
        String offerId;
        String offerName;
        String details;
        String startDate;
        String endDate;
        String referenceId;
        String restaurantId;
        String image;
        String action;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class OfferOutput {
        public OfferDetail.Response response;
        DB_Offers dbOffers;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
