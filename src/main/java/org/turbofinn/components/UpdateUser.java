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

public class UpdateUser implements RequestHandler<UpdateUser.UpdateUserInput, UpdateUser.UpdateUserOutput> {

    public static void main(String[] args) {
        UpdateUserInput updateUserInput = new UpdateUserInput();
        updateUserInput.setMobileNo("7269097225");
        DB_User newDetails = new DB_User();
        newDetails.setUserName("Tatsat1");
        newDetails.setEmail("tatsat@gmail.com");
        newDetails.setDob("2000-08-08");
        newDetails.setGender("Male");
        newDetails.setProfilePicture("Tatsat");
        updateUserInput.setNewDetails(newDetails);
        System.out.println(new Gson().toJson(new UpdateUser().handleRequest(updateUserInput, null)));
    }

    @Override
    public UpdateUserOutput handleRequest(UpdateUserInput updateUserInput, Context context) {
        if (updateUserInput == null || updateUserInput.mobileNo == null || updateUserInput.newDetails == null) {
            return new UpdateUserOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        DB_User dbUser = DB_User.fetchUserByMobileNo(updateUserInput.getMobileNo());
        if (dbUser == null) {
            return new UpdateUserOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        // Update user details
        updateDetails(dbUser, updateUserInput.getNewDetails());
        dbUser.setUpdatedDate(Date.from(Instant.now()));
        dbUser.save();

        return new UpdateUserOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), dbUser);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserInput {
        String mobileNo;
        DB_User newDetails; // Assuming this class contains fields to update
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserOutput {
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
