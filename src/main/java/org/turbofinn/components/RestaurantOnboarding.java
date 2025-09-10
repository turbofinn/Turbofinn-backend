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
                "  \"restaurantId\": \"123e4567-e89b-12d3-a456-426614174000\",\n" +
                "  \"bankDetails\": {\n" +
                "    \"AccountNumber\": \"123456789012\",\n" +
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
                    null
            );
        }
    }


    private OnboardingOutput stepBasicInfo(OnboardingInput input) {
        if(input.getBasicInfo() == null ||
                input.getBasicInfo().getRestaurantName() == null ||
                input.getBasicInfo().getContactNumber() == null ||
                input.getBasicInfo().getEmailId() == null){
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Missing required fields"),
                    null
            );
        }

        if (DB_Restaurant.fetchRestaurantByMobileNo(input.getBasicInfo().getContactNumber()) != null) {
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Contact number already registered"),
                    null
            );
        }
        if (DB_Restaurant.fetchRestaurantByEmailId(input.getBasicInfo().getEmailId()) != null) {
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Email already registered"),
                    null
            );
        }
        DB_Restaurant dbRestaurant = new DB_Restaurant();
        dbRestaurant.setRestaurantId(UUID.randomUUID().toString());
        dbRestaurant.setStatus(DB_Restaurant.StatusType.INACTIVE.toString());
        dbRestaurant.setName(input.getBasicInfo().getRestaurantName());
        dbRestaurant.setContactNo(input.getBasicInfo().getContactNumber());
        dbRestaurant.setEmailId(input.getBasicInfo().getEmailId());
        dbRestaurant.save();


        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE, "step BasicInfo Completed"),
                dbRestaurant.getRestaurantId()
        );
    }

    private OnboardingOutput stepProfileInfo(OnboardingInput input){
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if (dbRestaurant == null){
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"RestaurantId is not found"),
                    null
            );
        }
        Gson gson = new Gson();
        DB_Restaurant.ProfileInfo profile = input.getProfileInfo();

        if (profile.getCuisineTypes() != null) {
            String[] cuisines = profile.getCuisineTypes();

            if (profile.getCustomCuisine() != null && !profile.getCustomCuisine().isEmpty()) {
                String[] updated = Arrays.copyOf(cuisines, cuisines.length + 1);
                updated[cuisines.length] = profile.getCustomCuisine();
                cuisines = updated;
            }

            dbRestaurant.setCuisineTypes(gson.toJson(cuisines));
        }

        if (profile.getServiceTypes() != null) {
            dbRestaurant.setServiceTypes(gson.toJson(profile.getServiceTypes()));
        }
        if (profile.getOperatingHours() != null) {
            dbRestaurant.setOpeningTime(profile.getOperatingHours().getOpeningTime());
            dbRestaurant.setClosingTime(profile.getOperatingHours().getClosingTime());

        }
        dbRestaurant.setSeatingCapacity(profile.getSeatingCapacity());
        dbRestaurant.save();

        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE,"Step profile information completed"),
                dbRestaurant.getRestaurantId()
        );
    }

    private OnboardingOutput stepAddressInfo(OnboardingInput input){
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if(dbRestaurant == null){
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"RestaurantId is not found"),
                    null
            );
        }
        DB_Restaurant.AddressInfo address = input.getAddressInfo();
        dbRestaurant.setLatitude(address.getLatitude());
        dbRestaurant.setLongitude(address.getLongitude());
        dbRestaurant.save();

        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE, "Step address information completed"),
                dbRestaurant.getRestaurantId()
        );
    }
    private OnboardingOutput stepPaymentInfo(OnboardingInput input) {
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if(dbRestaurant == null) {
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "RestaurantId is not found"),
                    null
            );
        }

        DB_Restaurant.BankDetails bank = input.getBankDetails();
        dbRestaurant.setAccountNumber(bank.getAccountNumber());
        dbRestaurant.setIfscCode(bank.getIfscCode());
        dbRestaurant.setAccountHolderName(bank.getAccountHolderName());
        dbRestaurant.setBankName(bank.getBankName());
        dbRestaurant.save();

        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE, "Step payment information completed"),
                dbRestaurant.getRestaurantId()
        );
    }
    private OnboardingOutput completeOnboarding(OnboardingInput input) {
        DB_Restaurant dbRestaurant = DB_Restaurant.fetchRestaurantByID(input.getRestaurantId());
        if(dbRestaurant == null) {
            return new OnboardingOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "RestaurantId is not found"),
                    null
            );
        }

        dbRestaurant.setStatus(DB_Restaurant.StatusType.ACTIVE.toString());
        dbRestaurant.save();

        return new OnboardingOutput(
                new Response(Constants.SUCCESS_RESPONSE_CODE, "Onboarding completed successfully"),
                dbRestaurant.getRestaurantId()
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
        private String restaurantId;

    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private int responseCode;
        private String message;


    }

}
