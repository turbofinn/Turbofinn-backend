package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_AuthenticationOTP;
import org.turbofinn.dbmappers.DB_User;
import org.turbofinn.util.Constants;

import java.util.UUID;

public class SendOtp implements RequestHandler<SendOtp.SendOtpInput, SendOtp.SendOtpOutput> {
    public static void main(String[] args) {
        SendOtpInput sendOtpInput = new SendOtpInput();
        sendOtpInput.setMobileNo("7985157963");
        System.out.println(new SendOtp().handleRequest(sendOtpInput,null));
    }

    @Override
    public SendOtpOutput handleRequest(SendOtp.SendOtpInput input, Context context) {
        System.out.println("input "+ new Gson().toJson(input));
        if(input!=null || input.mobileNo!=null){
            DB_User dbUser = DB_User.fetchUserByMobileNo(input.mobileNo);
            System.out.println(dbUser);
        }
        return null;


    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class SendOtpOutput {
        public Response response;
    }


    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class SendOtpInput {
        public String mobileNo;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
