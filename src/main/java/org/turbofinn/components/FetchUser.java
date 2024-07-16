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

public class FetchUser implements RequestHandler<FetchUser.FetchUserInput, FetchUser.FetchUserOutput> {

    public static void main(String[] args) {
        FetchUserInput fetchUserInput = new FetchUserInput();
        fetchUserInput.setMobileNo("7269097225");
        System.out.println(new Gson().toJson(new FetchUser().handleRequest(fetchUserInput, null)));
    }

    @Override
    public FetchUserOutput handleRequest(FetchUserInput fetchUserInput, Context context) {
        if (fetchUserInput == null || fetchUserInput.mobileNo == null) {
            return new FetchUserOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        DB_User dbUser = DB_User.fetchUserByMobileNo(fetchUserInput.getMobileNo());
        if (dbUser == null) {
            return new FetchUserOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        return new FetchUserOutput(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), dbUser);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FetchUserInput {
        String mobileNo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FetchUserOutput {
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
}
