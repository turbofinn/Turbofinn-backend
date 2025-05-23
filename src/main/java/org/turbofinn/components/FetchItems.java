package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Items;
import org.turbofinn.util.Constants;

import java.util.List;

public class FetchItems implements RequestHandler<FetchItems.FetchItemsInput, FetchItems.FetchItemsOutput> {

    public static void main(String[] args) {
        // Fetch items by criteria example
//        FetchItemsInput input = new FetchItemsInput();
//        input.setMode("CRITERIA");
//        input.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
//        input.setCategory("Saafood1");
//        input.setTag("dineIn");
//        input.setUserId("9dfcbdb5-aafc-42fe-bef6-c919aabcca66");
//        System.out.println(new Gson().toJson(new FetchItems().handleRequest(input, null)));

//        // Fetch item by ID example
//        FetchItemsInput idInput = new FetchItemsInput();
//        idInput.setMode("BYID");
//        idInput.setItemId("0ad06ce2-ddf6-431a-9e09-a939e3fdce4b");
//        System.out.println(new Gson().toJson(new FetchItems().handleRequest(idInput, null)));

//        FetchItemsInput input = new FetchItemsInput();
//        input.setMode("BYRESTAURANTID");
//        input.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
//        input.setCategory("Seafood");
//        input.setUserId("9dfcbdb5-aafc-42fe-bef6-c919aabcca66");
//        System.out.println(new Gson().toJson(new FetchItems().handleRequest(input, null)));


    }

    @Override
    public FetchItemsOutput handleRequest(FetchItemsInput fetchItemsInput, Context context) {
        if (fetchItemsInput == null || fetchItemsInput.getMode() == null) {
            return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        switch(DB_Items.ActionType.getActionType(fetchItemsInput.mode)){
            case CRITERIA:
                return fetchItemsByCriteria(fetchItemsInput);
            case BYID:
                return fetchItemById(fetchItemsInput);
            case BYRESTAURANTID:
                return fetchItemsByRestaurantId(fetchItemsInput);
            default:
                return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);

        }

    }

    private FetchItemsOutput fetchItemsByRestaurantId(FetchItemsInput fetchItemsInput) {
        if (fetchItemsInput == null && fetchItemsInput.getRestaurantId() == null && fetchItemsInput.getRestaurantId().isBlank()) {
            return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        List<DB_Items> items = DB_Items.fetchItemsByRestaurantID(fetchItemsInput.getRestaurantId());

        if (fetchItemsInput.getCategory() != null) {
            items = items.stream()
                    .filter(x -> x.getCategory() != null && x.getCategory().equalsIgnoreCase(fetchItemsInput.getCategory()))
                    .toList();
            if (items.isEmpty()){
                return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "category not found"), null);
            }
        }
        return new FetchItemsOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),items);

    }

    private FetchItemsOutput fetchItemsByCriteria(FetchItemsInput fetchItemsInput) {
        if (fetchItemsInput.getRestaurantId() == null || fetchItemsInput.getTag() == null) {
            return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        List<DB_Items> items = DB_Items.fetchItemsByRestaurantIDAndTag(fetchItemsInput.getRestaurantId(), fetchItemsInput.getTag());

        if (fetchItemsInput.getCategory() != null) {
            items = items.stream()
                    .filter(x -> x.getCategory() != null && x.getCategory().equalsIgnoreCase(fetchItemsInput.getCategory()))
                    .toList();
            if (items.isEmpty()){
                return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "category not found"), null);
            }
        }


        if (fetchItemsInput.getFlag() != null) {
            items = items.stream()
                    .filter(x -> x.getFlag() != null && x.getFlag().equalsIgnoreCase(fetchItemsInput.getFlag()))
                    .toList();
            if(items.isEmpty()){
                return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "flag not found"), null);
            }
        }


        if (items.isEmpty()) {
            return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        return new FetchItemsOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), items);
    }

    private FetchItemsOutput fetchItemById(FetchItemsInput fetchItemsInput) {
        if (fetchItemsInput.getItemId() == null) {
            return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        DB_Items item = DB_Items.fetchItemByID(fetchItemsInput.getItemId());

        if (item == null) {
            return new FetchItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Item not found"), null);
        }

        return new FetchItemsOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), List.of(item));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FetchItemsOutput {
        public Response response;
        public List<DB_Items> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FetchItemsInput {
        public String mode; // "criteria" or "byId"
        public String userId;
        public String restaurantId;
        public String category;
        public String tag;
        public String flag;
        public String itemId; // Used for fetching by ID
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
