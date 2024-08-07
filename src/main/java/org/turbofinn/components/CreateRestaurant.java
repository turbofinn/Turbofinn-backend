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
import org.turbofinn.util.TFUtils;

public class CreateRestaurant implements RequestHandler<CreateRestaurant.CreateRestaurantInput,CreateRestaurant.CreateRestaurantOutput> {
    public static void main(String[] args) {
        CreateRestaurantInput input = new CreateRestaurantInput();
        input.setName("Ti");
        input.setAddressLineOne("45");
        input.setAddressLineTwo("Devine paradise");
        input.setCity("Gorakhpur");
        input.setState("Uttar Pradesh");
        input.setPincode("221404");
        input.setEmailId("Tiwari@gamil.com");
        input.setContactNo("7275583550");
        input.setRestaurantAccountNo("785412985632");
        input.setAction("CREATE");
        System.out.println(new Gson().toJson(new CreateRestaurant().handleRequest(input,null)));
    }

    @Override
    public CreateRestaurant.CreateRestaurantOutput handleRequest(CreateRestaurant.CreateRestaurantInput createRestaurantInput, Context context) {
        System.out.println("input " +new Gson().toJson(createRestaurantInput));
        if (createRestaurantInput==null){
            return new CreateRestaurantOutput(new Respone(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        switch (DB_Restaurant.ActionType.getActionType(createRestaurantInput.action)) {
            case CREATE:
                return createNewRestaurant(createRestaurantInput);
            case UPDATE:
                return updateRestaurant(createRestaurantInput);
            default:
                return new CreateRestaurantOutput(new Respone(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
    }

    public CreateRestaurant.CreateRestaurantOutput createNewRestaurant(CreateRestaurant.CreateRestaurantInput input){
//        add validations and regex
        DB_Restaurant dbRestaurant = new DB_Restaurant();
        dbRestaurant.setRestaurantId(input.getRestaurantId());
        dbRestaurant.setName(input.getName());
        dbRestaurant.setAddressLineOne(input.getAddressLineOne());
        dbRestaurant.setAddressLineTwo(input.getAddressLineTwo());
        dbRestaurant.setCity(input.getCity());
        dbRestaurant.setState(input.getState());
        dbRestaurant.setPincode(input.getPincode());
        dbRestaurant.setEmailId(input.getEmailId());
        dbRestaurant.setContactNo(input.getContactNo());
        dbRestaurant.setRestaurantAccountNo(TFUtils.generateRestaurantAccountNo(Constants.RESTAURANT_ACCOUNT_NO_LENGTH));
        dbRestaurant.save();
        return new CreateRestaurantOutput(new Respone(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), dbRestaurant);
    }

    public CreateRestaurant.CreateRestaurantOutput updateRestaurant(CreateRestaurant.CreateRestaurantInput input){
        if (input.restaurantId==null){
            return new CreateRestaurantOutput(new Respone(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
//        add validations and regex
//        return full json
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.restaurantId);
        dbRestaurant.setContactNo(input.contactNo);
        dbRestaurant.setName(input.name);
        dbRestaurant.setAddressLineOne(input.addressLineOne);
        dbRestaurant.setAddressLineTwo(input.addressLineTwo);
        dbRestaurant.setCity(input.city);
        dbRestaurant.setState(input.state);
        dbRestaurant.setPincode(input.pincode);
        dbRestaurant.setEmailId(input.emailId);
        dbRestaurant.setContactNo(input.contactNo);
        dbRestaurant.save();
        return new CreateRestaurantOutput(
                new Respone(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), dbRestaurant);
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateRestaurantInput {
        public String restaurantId;
        public String name;
        public String addressLineOne;
        public String addressLineTwo;
        public String city;
        public String state;
        public String pincode;
        public String emailId;
        public String contactNo;
        public String restaurantAccountNo;
        public String action;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateRestaurantOutput {
        public Respone respone;
        public DB_Restaurant dbRestaurant;
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public class Respone {
        int responseCode;
        String message;
    }
}

