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


public class FetchOffer implements RequestHandler<FetchOffer.FetchOfferInput, FetchOffer.FetchOfferOutput> {

    public static void main(String []args){
        FetchOfferInput idInput = new FetchOfferInput();
        idInput.setOfferId("567");
        idInput.setOfferName("Prime");
        idInput.setDetails("Mega Offer");
        idInput.setStartDate("24/07/2024");
        idInput.setEndDate("28/07/2024");
        idInput.setRestaurantId("1");
        idInput.setReferenceId("tegw");
        idInput.setImage("./mega.png");
        System.out.println(new Gson().toJson(new FetchOffer().handleRequest(idInput, null)));
    }

    @Override
    public FetchOffer.FetchOfferOutput handleRequest(FetchOffer.FetchOfferInput input, Context context) {
        if (input == null) {
            return new FetchOffer.FetchOfferOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        else {
            return fetchOfferById(input);
        }
    }

    private FetchOffer.FetchOfferOutput fetchOfferById(FetchOffer.FetchOfferInput input) {
        if (input.getOfferId() == null) {
            return new FetchOffer.FetchOfferOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        DB_Offers offer = DB_Offers.fetchOfferByID(input.getOfferId());

        if (offer == null) {
            return new FetchOffer.FetchOfferOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Item not found"), null);
        }
        return new FetchOffer.FetchOfferOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), offer);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FetchOfferInput {
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
    public static class FetchOfferOutput {
        public FetchOffer.Response response;
        DB_Offers dbOffers;
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
