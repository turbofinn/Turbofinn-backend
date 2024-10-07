package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Ingredients;
import org.turbofinn.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class IngredientsDetails implements RequestHandler<IngredientsDetails.IngredientsDetailsInput, IngredientsDetails.IngredientsDetailsOutput> {

    public static void main(String[] args) {
        String request = "{\n" +
                "    \"restaurantId\": \"308bc44a-de00-488e-b980-5ee0797e82e2\",\n" +
                "    \"itemId\": \"cd-7c6f-4e5e-b556-12c478e744gh\",\n" +
                "    \"action\": \"CREATE\",\n" +
                "    \"ingredientsList\": [\n" +
                "        {\n" +
                "            \"id\": \"ingredient1\",\n" +
                "            \"name\": \"Tomato\",\n" +
                "            \"quantity\": \"2\",\n" +
                "            \"unit\": \"kg\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ingredient2\",\n" +
                "            \"name\": \"Onion\",\n" +
                "            \"quantity\": \"1\",\n" +
                "            \"unit\": \"kg\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        System.out.println(new Gson().toJson(new IngredientsDetails().handleRequest(new Gson().fromJson(request, IngredientsDetails.IngredientsDetailsInput.class), null)));
    }

    @Override
    public IngredientsDetails.IngredientsDetailsOutput handleRequest(IngredientsDetails.IngredientsDetailsInput input, Context context) {
        System.out.println("input " +new Gson().toJson(input));
        if(input == null ){
            return new IngredientsDetails.IngredientsDetailsOutput( new IngredientsDetails.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        if(input.itemId == null){
            return new IngredientsDetails.IngredientsDetailsOutput( new IngredientsDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please add items to order"));
        }
        if(input.restaurantId == null){
            return new IngredientsDetails.IngredientsDetailsOutput( new IngredientsDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please add restaurantId to order"));
        }
        if(input.ingredientsList == null){
            return new IngredientsDetails.IngredientsDetailsOutput( new IngredientsDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please add ingredients to your food-item"));
        }

        switch (DB_Ingredients.ActionType.getActionType(input.action)) {
            case CREATE:
                return createNewIngredient(input);
            case UPDATE:
                return updateIngredients(input);
            default:
                return new IngredientsDetails.IngredientsDetailsOutput(new IngredientsDetails.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
    }

    public IngredientsDetails.IngredientsDetailsOutput createNewIngredient(IngredientsDetails.IngredientsDetailsInput input){
        DB_Ingredients dbIngredients = new DB_Ingredients();
        dbIngredients.setRestaurantId(input.getRestaurantId());
        dbIngredients.setItemId(input.getItemId());
        List<DB_Ingredients.Ingredients> ingredientsLists = new Gson().fromJson(
                new Gson().toJson(input.getIngredientsList()),
                new TypeToken<List<DB_Ingredients.Ingredients>>(){}.getType()
        );
        dbIngredients.setIngredients(new Gson().toJson(ingredientsLists));

        dbIngredients.save();
        return new IngredientsDetails.IngredientsDetailsOutput(new IngredientsDetails.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
    }

    public IngredientsDetails.IngredientsDetailsOutput updateIngredients(IngredientsDetails.IngredientsDetailsInput input){
        DB_Ingredients dbIngredients = DB_Ingredients.fetchIngredientsByRestaurantIDAndItemId(input.restaurantId, input.itemId);
        List<DB_Ingredients.Ingredients> ingredientsLists = new Gson().fromJson(
                new Gson().toJson(input.getIngredientsList()),
                new TypeToken<List<DB_Ingredients.Ingredients>>(){}.getType()
        );
        assert dbIngredients != null;
        dbIngredients.setIngredients(new Gson().toJson(ingredientsLists));
        dbIngredients.save();
        return new IngredientsDetails.IngredientsDetailsOutput(new IngredientsDetails.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientsDetailsOutput {
        public Response response;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class IngredientsDetailsInput {
        String restaurantId;
        String itemId;
        String action;
        ArrayList<DB_Ingredients.Ingredients> ingredientsList;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }
}
