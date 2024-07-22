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



import java.io.File;
import java.util.UUID;

public class CreateItems implements RequestHandler<CreateItems.CreateItemsInput,CreateItems.CreateItemsOutput> {

    public static void main(String[] args) {
        CreateItemsInput createItemsInput = new CreateItemsInput();
        createItemsInput.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
        createItemsInput.setItemId("bef2578c-8346-4b03-8971-20da77c4bedd");
        createItemsInput.setName("paneer chili dusre vale ka2");
        createItemsInput.setType("noodels");
        createItemsInput.setCuisine("chinese");
        createItemsInput.setCategory("paneer");
        createItemsInput.setFlag("veg");
        createItemsInput.setTag("dineIn");
        createItemsInput.setAction("CREATE");
        createItemsInput.setDescription("very delicious ,somkey hot ,korean noodles");
        createItemsInput.setPrice(150.0);
        createItemsInput.setEta("dineIn");
        createItemsInput.setItemPicture("C:\\Users\\saurabh\\Downloads\\sushi_platter.jpg");

        System.out.println(new Gson().toJson(new CreateItems().handleRequest(createItemsInput,null)));


    }

    @Override
    public CreateItems.CreateItemsOutput handleRequest(CreateItems.CreateItemsInput createItemsInput, Context context) {
        if(createItemsInput ==null ){
            return new CreateItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        if(createItemsInput.getRestaurantId()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide RestaurantId"));
        }

        switch (DB_Items.ActionType.getActionType(createItemsInput.action)) {
            case CREATE:
                return createNewItem(createItemsInput);
            case UPDATE:
                return updateItem(createItemsInput);
            case DELETE:
                return softDeleteItem(createItemsInput);
            default:
                return new CreateItemsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
    }

    private CreateItemsOutput softDeleteItem(CreateItemsInput createItemsInput) {
        if(createItemsInput.getItemId()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Item id is null"));
        }
        DB_Items dbItems = DB_Items.fetchItemByID(createItemsInput.itemId);
        dbItems.setIsDeleted("true");
        dbItems.save();

        return new CreateItemsOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
    }

    private CreateItemsOutput updateItem(CreateItemsInput createItemsInput) {
        if(createItemsInput.getName()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food name"));
        }
        if(createItemsInput.getType()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food type"));
        }
        if(createItemsInput.getCuisine()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food Cuisine"));
        }
        if(createItemsInput.getFlag()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food Flag"));
        }
        if(createItemsInput.getDescription()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food Description"));
        }
        if(createItemsInput.getPrice()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please set Food price"));
        }
        if(createItemsInput.getEta()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please set estimated time"));
        }
        if(createItemsInput.getTag()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food tag"));
        }
        if(createItemsInput.getItemPicture()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please set the food picture"));
        }
        if(createItemsInput.getItemId()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Item id is null"));
        }


        //logic for uploading image in s3
        String bucketName = "turbo-treats";
        String uuid = UUID.randomUUID().toString();
        String imageKey = "Images/" + uuid + ".jpg";
        File imageFile = new File(createItemsInput.getItemPicture());

        String imageUrl = ImageUploadUtil.uploadFile(bucketName, imageKey, imageFile);
        if (imageUrl == null) {
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE, "Failed to upload image to S3"));
        }


        DB_Items dbItems = DB_Items.fetchItemByID(createItemsInput.itemId);
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
        dbItems.setItemPicture(uuid);
        dbItems.save();
        return new CreateItemsOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));

    }

    private CreateItemsOutput createNewItem(CreateItemsInput createItemsInput) {
        if(createItemsInput.getName()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food name"));
        }
        if(createItemsInput.getType()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food type"));
        }
        if(createItemsInput.getCuisine()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food Cuisine"));
        }
        if(createItemsInput.getFlag()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food Flag"));
        }
        if(createItemsInput.getDescription()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food Description"));
        }
        if(createItemsInput.getPrice()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please set Food price"));
        }
        if(createItemsInput.getEta()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please set estimated time"));
        }
        if(createItemsInput.getTag()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Food tag"));
        }
        if(createItemsInput.getItemPicture()==null){
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please set the food picture"));
        }

        //logic for uploading image in s3
        String bucketName = "turbo-treats";
        String uuid = UUID.randomUUID().toString();
        String imageKey = "Images/" + uuid + ".jpg";
        File imageFile = new File(createItemsInput.getItemPicture());

        String imageUrl = ImageUploadUtil.uploadFile(bucketName, imageKey, imageFile);
        if (imageUrl == null) {
            return new CreateItemsOutput(new Response(Constants.GENERIC_RESPONSE_CODE, "Failed to upload image to S3"));
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
        dbItems.setItemPicture(uuid);
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
        String itemId;
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

        String action;

    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }

}
