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

public class CreateOffer implements RequestHandler<CreateOffer.CreateOfferInput, CreateOffer.CreateOfferOutput> {

    public static void main(String[] args){
        CreateOffer.CreateOfferInput input = new CreateOffer.CreateOfferInput();
        input.setOfferName("prime");
        input.setDetails("Mega offer");
        input.setStartDate("24/07/2024");
        input.setEndDate("28/07/2024");
        input.setReferenceId("qwe-rty");
        input.setRestaurantId("324");
        input.setImage("./mega.png");
        System.out.println(new Gson().toJson(input));
        System.out.println(new Gson().toJson(new CreateOffer().handleRequest(input,null)));
    }

    @Override
    public CreateOffer.CreateOfferOutput handleRequest(CreateOffer.CreateOfferInput input, Context context) {
        if(input == null){
            return new CreateOffer.CreateOfferOutput(new CreateOffer.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        return createNewOffer(input);
    }

    public CreateOffer.CreateOfferOutput createNewOffer(CreateOffer.CreateOfferInput input){
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
        return new CreateOffer.CreateOfferOutput(new CreateOffer.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), dbOffer);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOfferInput {
        String offerId;
        String offerName;
        String details;
        String startDate;
        String endDate;
        String referenceId;
        String restaurantId;
        String image;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateOfferOutput {
        public CreateOffer.Response response;
        DB_Offers dbOffers;
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
