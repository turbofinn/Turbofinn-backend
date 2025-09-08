package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.dbmappers.DB_Restaurant;
import org.turbofinn.util.Constants;

import java.util.Arrays;
import java.util.UUID;

public class RestaurantOnboarding implements RequestHandler<RestaurantOnboarding.OnboardingInput , RestaurantOnboarding.OnboardingOutput> {


    public static void main(String[] args) {
        RestaurantOnboarding handler = new RestaurantOnboarding();
        Gson gson = new Gson();

        String paymentInfoJson = "{\n" +
                "  \"step\": \"PAYMENT_INFO\",\n" +
                "  \"restaurantId\": \"4d150b73-f808-417d-898a-80703844e3aa\",\n" +
                "  \"bankDetails\": {\n" +
                "    \"accountNumber\": \"123456789012\",\n" +
                "    \"ifscCode\": \"HDFC0001234\",\n" +
                "    \"accountHolderName\": \"Local Test Cafe Owner\",\n" +
                "    \"bankName\": \"HDFC Bank\"\n" +
                "  }\n" +
                "}";


        OnboardingInput input = gson.fromJson(paymentInfoJson, OnboardingInput.class);


        OnboardingOutput output = handler.handleRequest(input, null);


        String responseJson = gson.toJson(output);
        System.out.println("Lambda Response:\n" + responseJson);
    }


    @Override
    public OnboardingOutput handleRequest(OnboardingInput input, Context context) {
        if(input == null || input.getStep() == null){
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),
                    null,
                    null,
                    null
            );

        }
        switch (input.getStep()) {
            case "BASIC_INFO":
                return stepBasicInfo(input);

            case "PROFILE_INFO":
                return stepProfileInfo(input);

            case "ADDRESS_INFO":
                return stepAddressInfo(input);

            case "PAYMENT_INFO":
                return stepPaymentInfo(input);

            case "COMPLETE":
                return completeOnboarding(input);
            default: return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Invalid step "),
                    null,
                    null,
                    null
            );
        }
    }


    private OnboardingOutput stepBasicInfo(OnboardingInput input) {
        if(input.getBasicInfo() == null ||
                input.getBasicInfo().getRestaurantName() == null ||
                input.getBasicInfo().getContactNumber() == null ||
                input.getBasicInfo().getEmail() == null){
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Missing required fields"),
                    null, null ,null
            );
        }
        DB_Restaurant dbRestaurant;
        if(input.getRestaurantId() == null){
            if(DB_Restaurant.fetchRestaurantByMobileNo(input.getBasicInfo().getContactNumber()) != null){
                return new OnboardingOutput(
                        new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Contact number already registered"),
                        null,null,null
                );
            }
            dbRestaurant = new DB_Restaurant();
            dbRestaurant.setRestaurantId(UUID.randomUUID().toString());
            dbRestaurant.setStatus(DB_Restaurant.StatusType.INACTIVE.toString());
        }else {
            dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
            if(dbRestaurant == null){
                return new OnboardingOutput(
                        new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Invalid restaurantId"),
                        null , null ,null
                );
            }
        }
        dbRestaurant.setName(input.getBasicInfo().getRestaurantName());
        dbRestaurant.setContactNo(input.getBasicInfo().getContactNumber());
        dbRestaurant.setEmailId(input.getBasicInfo().getEmail());
        dbRestaurant.save();


        return  new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE,"step BasicInfo Completed"),
                dbRestaurant ,
                dbRestaurant.getRestaurantId(),
                null
        );

    }

    private OnboardingOutput stepProfileInfo(OnboardingInput input){
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if (dbRestaurant == null){
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Invalid restaurantId"),
                    null , null , null
            );
        }
        DB_Restaurant.ProfileInfo profile = input.getProfileInfo();
        if (profile.getCuisineTypes() != null) {
            dbRestaurant.setCuisineTypes(Arrays.asList(profile.getCuisineTypes()));

            if (profile.getCustomCuisine() != null && !profile.getCustomCuisine().isEmpty()) {
                dbRestaurant.getCuisineTypes().add(profile.getCustomCuisine());
            }
        }
        if (profile.getServiceTypes() != null) {
            dbRestaurant.setServiceTypes(Arrays.asList(profile.getServiceTypes()));
        }
        if (profile.getOperatingHours() != null) {
            dbRestaurant.setOpenTime(profile.getOperatingHours().getOpenTime());
            dbRestaurant.setCloseTime(profile.getOperatingHours().getCloseTime());

        }
        dbRestaurant.setSeatingCapacity(profile.getSeatingCapacity());
        dbRestaurant.save();

        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE,"Step PROFILE_INFO completed"),
                dbRestaurant,dbRestaurant.getRestaurantId(),null
        );
    }

    private OnboardingOutput stepAddressInfo(OnboardingInput input){
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if(dbRestaurant == null){
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Invalid sessionId"),
                    null , null , null
            );
        }
        DB_Restaurant.AddressInfo address = input.getAddressInfo();
        dbRestaurant.setLatitude(address.getLatitude());
        dbRestaurant.setLongitude(address.getLongitude());
        dbRestaurant.save();

        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE, "Step ADDRESS_INFO completed"),
                dbRestaurant, dbRestaurant.getRestaurantId(), null
        );
    }
    private OnboardingOutput stepPaymentInfo(OnboardingInput input) {
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if(dbRestaurant == null) {
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Invalid sessionId"),
                    null, null, null
            );
        }

        DB_Restaurant.BankDetails bank = input.getBankDetails();
        dbRestaurant.setAccountNumber(bank.getAccountNumber());
        dbRestaurant.setIfscCode(bank.getIfscCode());
        dbRestaurant.setAccountHolderName(bank.getAccountHolderName());
        dbRestaurant.setBankName(bank.getBankName());
        dbRestaurant.save();

        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE, "Step PAYMENT_INFO completed"),
                dbRestaurant, dbRestaurant.getRestaurantId(), null
        );
    }
    private OnboardingOutput completeOnboarding(OnboardingInput input) {
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if(dbRestaurant == null) {
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Invalid sessionId"),
                    null, null, null
            );
        }

        dbRestaurant.setStatus(DB_Restaurant.StatusType.ACTIVE.toString());
        dbRestaurant.save();

        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE, "Onboarding completed successfully"),
                dbRestaurant,
                dbRestaurant.getRestaurantId(),
                "/dashboard"
        );
    }



    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class OnboardingInput {
        private String step;
        private String restaurantId;

        private DB_Restaurant.BasicInfo basicInfo;
        private DB_Restaurant.ProfileInfo profileInfo;
        private DB_Restaurant.AddressInfo addressInfo;
        private DB_Restaurant.BankDetails bankDetails;



    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OnboardingOutput {
        private Response response;
        private DB_Restaurant dbRestaurant;
        private String restaurantId;
        private String dashboardUrl;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private int responseCode;
        private String message;


    }

}
