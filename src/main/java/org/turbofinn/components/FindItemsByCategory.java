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

public class FindItemsByCategory implements RequestHandler<FindItemsByCategory.FindItemsByCategoryInput,FindItemsByCategory.FindItemsByCategoryOutput> {
    public static void main(String[] args) {
        FindItemsByCategoryInput findItemsByCategoryInput = new FindItemsByCategoryInput();
        findItemsByCategoryInput.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
        findItemsByCategoryInput.setCategory("paneer");
        System.out.println(new Gson().toJson(new FindItemsByCategory().handleRequest(findItemsByCategoryInput,null)));
    }

    @Override
    public FindItemsByCategory.FindItemsByCategoryOutput handleRequest(FindItemsByCategory.FindItemsByCategoryInput findItemsByCategoryInput, Context context) {
        if(findItemsByCategoryInput == null || findItemsByCategoryInput.restaurantId==null || findItemsByCategoryInput.category==null){
            return new FindItemsByCategoryOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);

        }
        List<DB_Items> items = DB_Items.fetchItemsByRestaurantIDAndCategory(findItemsByCategoryInput.getRestaurantId(), findItemsByCategoryInput.getCategory());

        if(items.isEmpty()){
            return new FindItemsByCategoryOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }


        return new FindItemsByCategoryOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),items);
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class FindItemsByCategoryOutput {
        public Response respone;
        public List<DB_Items> items;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class FindItemsByCategoryInput {
        public String restaurantId;
        public String category;
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response{
        int responseCode;
        String message;
    }

}
