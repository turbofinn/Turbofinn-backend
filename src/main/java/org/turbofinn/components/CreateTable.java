package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.aws.AWSCredentials;
import org.turbofinn.dbmappers.DB_Restaurant;
import org.turbofinn.dbmappers.DB_Table;
import org.turbofinn.util.Constants;

public class CreateTable implements RequestHandler<CreateTable.CreateTableInput, CreateTable.CreateTableOutput> {

    public static void main(String[] args) {
        String request = "{\n" +
                "    \"tableNo\": \"9\",\n" +
                "    \"restaurantId\": \"fa5f6d2d-7358-4bd0-a28c-25cd32051ebc\",\n" +
                "    \"userId\": \"\",\n" +
                "    \"status\": \"unoccupied\",\n" +
                "    \"paymentStatus\": \"\",\n" +
                "    \"action\": \"DELETE\",\n" +
                "    \"mobileNo\": \"\"\n" +
                "}\n";
        System.out.println(new Gson().toJson(new CreateTable().handleRequest(new Gson().fromJson(request, CreateTableInput.class), null)));
    }

    @Override
    public CreateTableOutput handleRequest(CreateTableInput input, Context context) {
        System.out.println("Input: " + new Gson().toJson(input));

        if (input == null || input.getAction() == null) {
            return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }

        switch (ActionType.getActionType(input.getAction())) {
            case CREATE:
                return createNewTable(input);
            case UPDATE:
                return updateTable(input);
            case DELETE:
                return deleteTable(input);
            default:
                return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
    }

    public CreateTableOutput createNewTable(CreateTableInput input) {
        DB_Table dbTable = new DB_Table();
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        dbRestaurant.setTableCount(String.valueOf(Integer.parseInt(dbRestaurant.getTableCount()) + 1));
        dbTable.setTableNo(dbRestaurant.getTableCount());
        dbTable.setRestaurantId(input.getRestaurantId());
        dbTable.setUserId(input.getUserId());
        dbTable.setStatus(input.getStatus());
        dbTable.setPaymentStatus(input.getPaymentStatus());
        dbTable.setMobileNo(input.getMobileNo());

        try {
            dbTable.save();
            dbRestaurant.save();
            return new CreateTableOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE));
        } catch (Exception e) {
            return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, e.getMessage()));
        }
    }

    public CreateTableOutput updateTable(CreateTableInput input) {
        try {
            DB_Table dbTable = DB_Table.fetchByTableNo(input.getTableNo(), input.getRestaurantId());
            if (dbTable == null) {
                return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Table not found"));
            }

            dbTable.setTableNo(input.getTableNo());
            dbTable.setRestaurantId(input.getRestaurantId());
            dbTable.setUserId(input.getUserId());
            dbTable.setStatus(input.getStatus());
            dbTable.setPaymentStatus(input.getPaymentStatus());
            dbTable.setMobileNo(input.getMobileNo());
            dbTable.save();
            System.out.println(new Gson().toJson(dbTable));
            return new CreateTableOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE));
        } catch (Exception e) {
            return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, e.getMessage()));
        }
    }

    public CreateTableOutput deleteTable(CreateTableInput input) {
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        DB_Table dbTable = DB_Table.fetchByTableNo(dbRestaurant.getTableCount(), input.getRestaurantId());
        if (dbTable == null) {
            return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Table not found"));
        }
        dbRestaurant.setTableCount(String.valueOf(Integer.parseInt(dbRestaurant.getTableCount())-1));
        dbRestaurant.save();
        dbTable.deleteTable();

        return new CreateTableOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTableOutput {
        private Response response;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTableInput {
        private String tableNo;
        private String restaurantId;
        private String userId;
        private String status;
        private String paymentStatus;
        private String action;
        private String mobileNo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private int responseCode;
        private String message;
    }

    public enum ActionType {
        CREATE("CREATE"),
        UPDATE("UPDATE"),
        DELETE("DELETE");

        private String text;

        ActionType(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public static ActionType getActionType(String type) {
            if (type == null) {
                return null;
            }
            for (ActionType actionType : ActionType.values()) {
                if (actionType.text.equals(type)) {
                    return actionType;
                }
            }
            return null;
        }
    }
}
