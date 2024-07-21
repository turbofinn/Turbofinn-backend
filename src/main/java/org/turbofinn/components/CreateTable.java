package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Table;
import org.turbofinn.util.Constants;

public class CreateTable implements RequestHandler<CreateTable.CreateTableInput, CreateTable.CreateTableOutput> {

    public static void main(String[] args) {
        String request = "{\n" +
                "    \"tableId\": \"tableId123\",\n" +
                "    \"tableNo\": \"T001\",\n" +
                "    \"restaurantId\": \"restaurantId456\",\n" +
                "    \"userId\": \"userId789\",\n" +
                "    \"action\": \"CREATE\"\n" +
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
            default:
                return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
    }

    public CreateTableOutput createNewTable(CreateTableInput input) {
        DB_Table dbTable = new DB_Table();
        dbTable.setTableNo(input.getTableNo());
        dbTable.setRestaurantId(input.getRestaurantId());
        dbTable.setUserId(input.getUserId());

        try {
            dbTable.save();
            return new CreateTableOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE));
        } catch (Exception e) {
            return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, e.getMessage()));
        }
    }

    public CreateTableOutput updateTable(CreateTableInput input) {
        try {
            DB_Table dbTable = DB_Table.fetchByTableId(input.getTableId());
            if (dbTable == null) {
                return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Table not found"));
            }

            dbTable.setTableNo(input.getTableNo());
            dbTable.setRestaurantId(input.getRestaurantId());
            dbTable.setUserId(input.getUserId());
            dbTable.save();
            return new CreateTableOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE));
        } catch (Exception e) {
            return new CreateTableOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, e.getMessage()));
        }
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
        private String tableId;
        private String tableNo;
        private String restaurantId;
        private String userId;
        private String action;
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
        UPDATE("UPDATE");

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
