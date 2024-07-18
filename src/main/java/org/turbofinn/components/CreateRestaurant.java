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
        input.setAddress("Ashok nagar suriawan");
        input.setCity("Gorakhpur");
        input.setState("Uttar Pradesh");
        input.setPincode("221404");
        input.setEmailId("Tiwari@gamil.com");
        input.setContactNo("7275583550");
        input.setRestaurntAccountNo("785412985632");
        System.out.println(new Gson().toJson(new CreateRestaurant().handleRequest(input,null)));


    }

    @Override
    public CreateRestaurant.CreateRestaurantOutput handleRequest(CreateRestaurant.CreateRestaurantInput createRestaurantInput, Context context) {
        if (createRestaurantInput==null){
            return new CreateRestaurantOutput(new Respone(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        DB_Restaurant dbRestaurant = new DB_Restaurant();
        dbRestaurant.setName(createRestaurantInput.getName());
        dbRestaurant.setAddress(createRestaurantInput.getAddress());
        dbRestaurant.setCity(createRestaurantInput.getCity());
        dbRestaurant.setPincode(createRestaurantInput.getPincode());
        dbRestaurant.setEmailId(createRestaurantInput.getEmailId());
        dbRestaurant.setContactNo(createRestaurantInput.getContactNo());
        dbRestaurant.setRestaurntAccountNo(createRestaurantInput.getRestaurntAccountNo());
        dbRestaurant.save();
        return new CreateRestaurantOutput(new Respone(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateRestaurantInput {
        public String restaurantId;
        public String name;
        public String address;
        public String city;
        public String state;
        public String pincode;
        public String emailId;
        public String contactNo;
        public String restaurntAccountNo;
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

