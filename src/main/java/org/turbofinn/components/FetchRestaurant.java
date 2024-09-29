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

public class FetchRestaurant implements RequestHandler<FetchRestaurant.FetchRestaurantInput, FetchRestaurant.FetchRestaurantOutput> {

    public static void main(String[] args) {
        FetchRestaurantInput input = new FetchRestaurantInput();
        input.setMobileNo("7275583550");
        System.out.println(new Gson().toJson(new FetchRestaurant().handleRequest(input,null)));
    }



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
        restaurantDetails.setAddress(restaurant.getAddress());
        restaurantDetails.setCity(restaurant.getCity());
        restaurantDetails.setState(restaurant.getState());
        restaurantDetails.setPincode(restaurant.getPincode());
        restaurantDetails.setEmailId(restaurant.getEmailId());
        restaurantDetails.setContactNo(restaurant.getContactNo());
        restaurantDetails.setTableCount(restaurant.getTableCount());
        restaurantDetails.setLogo(restaurant.getLogo());
        restaurantDetails.setRestaurantAccountNo(restaurant.getRestaurantAccountNo());
        restaurantDetails.setStatus(restaurant.getStatus());

        return new FetchRestaurantOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, "Restaurant details fetched successfully"), restaurantDetails);
    }


    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class FetchRestaurantInput {
        public String mobileNo;
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class RestaurantDetails{
        String restaurantId;
        String name;
        String address;
        String city;
        String state;
        String pincode;
        String emailId;
        String contactNo;
        String restaurantAccountNo;
        String tableCount;
        String logo;
        String status;
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class FetchRestaurantOutput {
        public Response response;
        public RestaurantDetails restaurantDetails;

    }
    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class Response{
        public int responseCode;
        public String message;
    }

}


