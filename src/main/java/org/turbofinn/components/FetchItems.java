package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Items;
import org.turbofinn.dbmappers.DB_Restaurant;
import org.turbofinn.util.Constants;


import java.util.List;

public class FetchItems implements RequestHandler<FetchItems.FetchItemsInput,FetchItems.FetchItemsOutput> {

    public static void main(String[] args) {
        FetchItemsInput input = new FetchItemsInput();
        input.setRestaurantId("5cef7136-ff4e-418a-ba00-5ef18d67bd43");
        System.out.println(new Gson().toJson(new FetchItems().handleRequest(input,null)));
    }
    @Override
    public FetchItems.FetchItemsOutput handleRequest(FetchItems.FetchItemsInput fetchItemsInput, Context context) {
        if(fetchItemsInput==null || fetchItemsInput.restaurantId==null){
            return new FetchItems.FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);

        }

        List<DB_Items> items= DB_Items.fetchItemsByRestaurantID(fetchItemsInput.getRestaurantId());
        if (items.isEmpty()){
            return new FetchItems.FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }

        return new FetchItemsOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),items);
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class FetchItemsOutput {
        public Response response;
        public List<DB_Items> items;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class FetchItemsInput {
        public String restaurantId;
    }
    @Getter@Setter@AllArgsConstructor@NoArgsConstructor
    public static  class Response{
        int responseCode;
        String message;
    }
}
