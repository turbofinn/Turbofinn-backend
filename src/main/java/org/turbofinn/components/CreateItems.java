package org.turbofinn.components;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Items;
import org.turbofinn.util.Constants;

public class CreateItems implements RequestHandler<CreateItems.CreateItemsInput,CreateItems.CreateItemsOutput> {

    public static void main(String[] args) {
        CreateItemsInput createItemsInput = new CreateItemsInput();
        createItemsInput.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
        createItemsInput.setName("paneer chili dusre vale ka");
        createItemsInput.setType("noodels");
        createItemsInput.setCuisine("chinese");
        createItemsInput.setCategory("paneer");
        createItemsInput.setFlag("veg");
        createItemsInput.setTag("dineIn");
        createItemsInput.setDescription("very delicious ,somkey hot ,korean noodles");
        createItemsInput.setPrice(150.0);
        createItemsInput.setEta("dineIn");
        createItemsInput.setItemPicture("dineIn");
        System.out.println(new Gson().toJson(new CreateItems().handleRequest(createItemsInput,null)));
    }

    @Override
    public CreateItems.CreateItemsOutput handleRequest(CreateItems.CreateItemsInput createItemsInput, Context context) {
        if(createItemsInput ==null){
            return new CreateItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        DB_Items dbItems = new DB_Items();
        dbItems.setRestaurantId(createItemsInput.getRestaurantId());
        dbItems.setName(createItemsInput.getName());
        dbItems.setType(createItemsInput.getType());
        dbItems.setCuisine(createItemsInput.getCuisine());
        dbItems.setCategory(createItemsInput.getCategory());
        dbItems.setFlag(createItemsInput.getFlag());
        dbItems.setTag(createItemsInput.getTag());
        dbItems.setDescription(createItemsInput.getDescription());
        dbItems.setPrice(createItemsInput.getPrice());
        dbItems.setEta(createItemsInput.getEta());
        dbItems.setItemPicture(createItemsInput.getItemPicture());
        dbItems.save();
        return new CreateItemsOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateItemsOutput {
        public Response response;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateItemsInput {
        String restaurantId;
        String name;
        String type;        //  food , beverage ,bakery
        String cuisine;     // Indian, chinese,japanese
        String category;    // fastfood, chinese, biryani, pizza ,burger
        String flag;         //veg ,nonveg ,alcoholic,nonalcoholic
        String tag;
        String description;
        Double price;
        String eta;
        String itemPicture;

    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }

}
