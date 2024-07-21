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

public class CreateRestaurant implements RequestHandler<CreateRestaurant.CreateRestaurantInput,CreateRestaurant.CreateRestaurantOutput> {
    public static void main(String[] args) {
        CreateRestaurantInput input = new CreateRestaurantInput();
        input.setName("Tiwari");
        input.setFlatHouseNumber("45");
        input.setStreet("Mykonous");
        input.setLandmark("church street");
        input.setCity("Gorakhpur");
        input.setState("Uttar Pradesh");
        input.setPincode("221404");
        input.setEmailId("Tiwari@gamil.com");
        input.setContactNo("7275583550");
        input.setRestaurantAccountNo("785412985632");
        System.out.println(new Gson().toJson(new CreateRestaurant().handleRequest(input,null)));


    }

    @Override
    public CreateRestaurant.CreateRestaurantOutput handleRequest(CreateRestaurant.CreateRestaurantInput createRestaurantInput, Context context) {
        System.out.println("input " +new Gson().toJson(createRestaurantInput));
        if (createRestaurantInput==null){
            return new CreateRestaurantOutput(new Respone(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }

        switch (DB_Restaurant.ActionType.getActionType(createRestaurantInput.action)) {
            case CREATE:
                return createNewRestaurant(createRestaurantInput);
            case UPDATE:
                return updateRestaurant(createRestaurantInput);
            default:
                return new CreateRestaurantOutput(new Respone(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
    }

    public CreateRestaurant.CreateRestaurantOutput createNewRestaurant(CreateRestaurant.CreateRestaurantInput input){
        DB_Restaurant dbRestaurant = new DB_Restaurant();
        dbRestaurant.setRestaurantId(input.getRestaurantId());
        dbRestaurant.setName(input.getName());
        dbRestaurant.setFlatHouseNumber(input.getFlatHouseNumber());
        dbRestaurant.setStreet(input.getStreet());
        dbRestaurant.setLandmark(input.getLandmark());
        dbRestaurant.setCity(input.getCity());
        dbRestaurant.setState(input.getState());
        dbRestaurant.setPincode(input.getPincode());
        dbRestaurant.setEmailId(input.getEmailId());
        dbRestaurant.setContactNo(input.getContactNo());
        dbRestaurant.setRestaurantAccountNo(input.getRestaurantAccountNo());
        dbRestaurant.save();
        return new CreateRestaurantOutput(new Respone(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
    }
    public CreateRestaurant.CreateRestaurantOutput updateRestaurant(CreateRestaurant.CreateRestaurantInput input){
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.restaurantId);
        dbRestaurant.setContactNo(input.contactNo);
        dbRestaurant.setName(input.name);
        dbRestaurant.setFlatHouseNumber(input.flatHouseNumber);
        dbRestaurant.setStreet(input.street);
        dbRestaurant.setLandmark(input.landmark);
        dbRestaurant.setCity(input.city);
        dbRestaurant.setState(input.state);
        dbRestaurant.setPincode(input.pincode);
        dbRestaurant.setEmailId(input.emailId);
        dbRestaurant.setContactNo(input.contactNo);
        dbRestaurant.setRestaurantAccountNo(input.restaurantAccountNo);
        dbRestaurant.save();
        return new CreateRestaurantOutput(new Respone(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateRestaurantInput {
        public String restaurantId;
        public String name;
        public String flatHouseNumber;
        public String street;
        public String landmark;
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
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public class Respone {
        int responseCode;
        String message;
    }
}

