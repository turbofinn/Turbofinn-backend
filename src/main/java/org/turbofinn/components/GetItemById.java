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

public class GetItemById implements RequestHandler<GetItemById.GetItemByIdInput,GetItemById.GetItemByIdOutput> {

    public static void main(String[] args) {
        var input = new GetItemByIdInput();
        input.setItemId("ee5a149c-dbe2-4418-8573-902edcb73dce");
        input.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
        System.out.println(new Gson().toJson(new GetItemById().handleRequest(input,null)));
    }
    @Override
    public GetItemById.GetItemByIdOutput handleRequest(GetItemById.GetItemByIdInput input, Context context) {
        if(input==null || input.itemId==null || input.restaurantId==null){
            return new GetItemById.GetItemByIdOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }
        var item = DB_Items.fetchItemByID(input.itemId);
        if(item==null){
            return new GetItemById.GetItemByIdOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"invalid Item Request"),null);
        }


        return new GetItemById.GetItemByIdOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),item);
    }


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetItemByIdOutput {
        public Response response;
        public DB_Items db_items;
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class GetItemByIdInput {
        public String restaurantId;
        public String itemId;
    }
    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    private static class Response{
        int responseCode;
        String message;
    }
}
