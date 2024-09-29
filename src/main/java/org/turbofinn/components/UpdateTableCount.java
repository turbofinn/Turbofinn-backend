package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Restaurant;
import org.turbofinn.util.Constants;

public class UpdateTableCount implements RequestHandler<UpdateTableCount.UpdateTableCountInput, UpdateTableCount.UpdateTableCountOutput> {

    public static void main(String[] args) {

        String request = "{\n" + "    \"restaurantId\": \"fa5f6d2d-7358-4bd0-a28c-25cd32051ebc\",\n" + "    \"tableCount\": 10,\n" + "    \"action\": \"DELETE\"\n" + "}";
        System.out.println(new Gson().toJson(new UpdateTableCount().handleRequest(new Gson().fromJson(request, UpdateTableCountInput.class), null)));
    }

    @Override
    public UpdateTableCountOutput handleRequest(UpdateTableCountInput input, Context context) {
        System.out.println("Input: " + new Gson().toJson(input));

        if (input == null || input.getRestaurantId() == null || input.getRestaurantId().isEmpty()) {
            return new UpdateTableCountOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Restaurant ID is missing"), null, input.getTableCount());
        }
        if (input.getTableCount() == 0 && input.getAction().equals("DELETE")) {
            return new UpdateTableCountOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "No Table Present"), null, input.getTableCount());
        }

        // Fetch the restaurant from the database using the restaurant ID
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if (dbRestaurant == null) {
            return new UpdateTableCountOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Restaurant not found"), null, input.getTableCount());
        }


        // Update the table count and save it to the database
        if (input.getAction().equals("ADD")) {
            dbRestaurant.setTableCount(String.valueOf(input.getTableCount() + 1));
        } else if (input.getAction().equals("DELETE")) {
            dbRestaurant.setTableCount(String.valueOf(input.getTableCount() - 1));
        }
        dbRestaurant.save();

        return new UpdateTableCountOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, "Table count updated successfully"), input.getRestaurantId(), Integer.parseInt(dbRestaurant.getTableCount()));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTableCountInput {
        private String restaurantId;
        private int tableCount;
        private String action;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTableCountOutput {
        private Response response;
        private String restaurantId;
        private int newTableCount;
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

