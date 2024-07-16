package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_User;
import org.turbofinn.util.Constants;

import java.time.Instant;
import java.util.Date;

public class FetchUserDetails implements RequestHandler<FetchUserDetails.UserProfileInput, FetchUserDetails.UserProfileOutput> {

    public static void main(String[] args) {
          //Example for fetching user
         UserProfileInput fetchInput = new UserProfileInput("7269097225", "fetch");
         System.out.println(new Gson().toJson(new FetchUserDetails().handleRequest(fetchInput, null)));

        // Example for updating user
        DB_User newDetails = new DB_User();
        newDetails.setUserName("Updated Name");
        newDetails.setEmail("updatedemail@example.com");
        UserProfileInput updateInput = new UserProfileInput("7269097224", "update", newDetails);
        System.out.println(new Gson().toJson(new FetchUserDetails().handleRequest(updateInput, null)));
    }

    @Override
    public UserProfileOutput handleRequest(UserProfileInput userProfileInput, Context context) {
        if (userProfileInput == null || userProfileInput.getMobileNo() == null || userProfileInput.getAction() == null) {
            return new UserProfileOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        if ("fetch".equalsIgnoreCase(userProfileInput.getAction())) {
            return handleFetchRequest(userProfileInput);
        } else if ("update".equalsIgnoreCase(userProfileInput.getAction())) {
            return handleUpdateRequest(userProfileInput);
        } else {
            return new UserProfileOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Invalid action"), null);
        }
    }

    private UserProfileOutput handleFetchRequest(UserProfileInput userProfileInput) {
        DB_User dbUser = DB_User.fetchUserByMobileNo(userProfileInput.getMobileNo());
        if (dbUser == null) {
            return new UserProfileOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        return new UserProfileOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), dbUser);
    }

    private UserProfileOutput handleUpdateRequest(UserProfileInput userProfileInput) {
        if (userProfileInput.getNewDetails() == null) {
            return new UserProfileOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        DB_User dbUser = DB_User.fetchUserByMobileNo(userProfileInput.getMobileNo());
        if (dbUser == null) {
            return new UserProfileOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        // Update user details
        updateDetails(dbUser, userProfileInput.getNewDetails());
        dbUser.setUpdatedDate(Date.from(Instant.now()));
        dbUser.save();

        return new UserProfileOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), dbUser);
    }

    @Getter
    @Setter
    public static class UserProfileInput {
        String mobileNo;
        String action; // "fetch" or "update"
        DB_User newDetails; // Details for update

        // Constructor for fetch action
        public UserProfileInput(String mobileNo, String action) {
            this.mobileNo = mobileNo;
            this.action = action;
        }

        // Constructor for update action
        public UserProfileInput(String mobileNo, String action, DB_User newDetails) {
            this.mobileNo = mobileNo;
            this.action = action;
            this.newDetails = newDetails;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileOutput {
        Response response;
        DB_User user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }

    private void updateDetails(DB_User dbUser, DB_User newDetails) {
        if (newDetails.getUserName() != null) dbUser.setUserName(newDetails.getUserName());
        if (newDetails.getEmail() != null) dbUser.setEmail(newDetails.getEmail());
        if (newDetails.getGender() != null) dbUser.setGender(newDetails.getGender());
        if (newDetails.getDob() != null) dbUser.setDob(newDetails.getDob());
        if (newDetails.getProfilePicture() != null) dbUser.setProfilePicture(newDetails.getProfilePicture());
    }
}
