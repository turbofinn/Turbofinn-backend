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

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class SendOtp implements RequestHandler<SendOtp.SendOtpInput, SendOtp.SendOtpOutput> {
    public static void main(String[] args) {
        SendOtpInput sendOtpInput = new SendOtpInput();
        sendOtpInput.setMobileNo("7985257933");
        System.out.println(new Gson().toJson(new SendOtp().handleRequest(sendOtpInput,null)));
    }

    @Override
    public SendOtpOutput handleRequest(SendOtp.SendOtpInput input, Context context) {
        System.out.println("input "+ new Gson().toJson(input));
        if(input==null || input.mobileNo==null){
            return new SendOtpOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }

        DB_AuthenticationOTP a_otp = DB_AuthenticationOTP.fetchOtpByMobileNo(input.mobileNo);
        if (a_otp==null){
//            String otp = String.format("%04d", new Random().nextInt(10000));
            String otp="1234";
            long expirationTime = Instant.now().getEpochSecond() + 300;
            DB_AuthenticationOTP dbAuthenticationOTP = new DB_AuthenticationOTP();
            dbAuthenticationOTP.setMobileNo(input.mobileNo);
            dbAuthenticationOTP.setOtp(otp);
            dbAuthenticationOTP.setName(input.userName);
            dbAuthenticationOTP.setEmailId(input.emailId);
            dbAuthenticationOTP.setExpirationTime(expirationTime);
            dbAuthenticationOTP.save();
            return new SendOtpOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),otp);
        }
        else {
            return new SendOtpOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),a_otp.getOtp());
        }




    }


    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class SendOtpOutput {
        public Response response;
        public String otp;
    }


    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class SendOtpInput {
        public String mobileNo;
        public String userName;
        public String emailId;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
