package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Restaurant;
import org.turbofinn.util.Constants;

public class FetchRestaurant implements RequestHandler<FetchRestaurant.FetchRestaurantInput, FetchRestaurant.FetchRestaurantOutput> {
    @Override
    public FetchRestaurant.FetchRestaurantOutput handleRequest(FetchRestaurant.FetchRestaurantInput input, Context context) {

        if(input==null || input.mobileNo==null || input.mobileNo.isBlank()){
            return new FetchRestaurantOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Please give valid input"),null);
        }

        DB_Restaurant restaurant = DB_Restaurant.fetchRestaurantByMobileNo(input.mobileNo);
        if(restaurant==null){
            return new FetchRestaurantOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Please Enter vaild number"),null);
        }
        RestaurantDetails restaurantDetails = new RestaurantDetails();
        restaurantDetails.setRestaurantId(restaurant.getRestaurantId());
        restaurantDetails.setName(restaurant.getName());
        restaurantDetails.setAddressLineOne(restaurant.getAddressLineOne());
        restaurantDetails.setAddressLineTwo(restaurant.getAddressLineTwo());
        restaurantDetails.setCity(restaurant.getCity());
        restaurantDetails.setState(restaurant.getState());
        restaurantDetails.setPincode(restaurant.getPincode());
        restaurantDetails.setEmailId(restaurant.getEmailId());
        restaurantDetails.setContactNo(restaurant.getContactNo());
        restaurantDetails.setRestaurantAccountNo(restaurant.getRestaurantAccountNo());
        restaurantDetails.setStatus(restaurant.getStatus());

        return new FetchRestaurantOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, "Restaurant details fetched successfully"), restaurantDetails);
    }


    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public class FetchRestaurantInput {
        public String mobileNo;
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public class RestaurantDetails{
        String restaurantId;
        String name;
        String addressLineOne;
        String addressLineTwo;
        String city;
        String state;
        String pincode;
        String emailId;
        String contactNo;
        String restaurantAccountNo;
        String status;
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public class FetchRestaurantOutput {
        public Response response;
        public RestaurantDetails restaurantDetails;

    }
    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public class Response{
        public int responseCode;
        public String message;
    }

}


