package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_AuthenticationOTP;
import org.turbofinn.util.Constants;

import java.util.UUID;

public class SendOtp implements RequestHandler<SendOtp.SendOtpInput, SendOtp.SendOtpOutput> {
    @Override
    public SendOtpOutput handleRequest(SendOtp.SendOtpInput input, Context context) {
        System.out.println("input "+ new Gson().toJson(input));
        if(input==null || input.mobileNo==null){
            DB_AuthenticationOTP dbAuthenticationOTP = new DB_AuthenticationOTP();
            dbAuthenticationOTP.setDeviceId(UUID.randomUUID().toString());
            dbAuthenticationOTP.setMobileNo("8960880615");
            dbAuthenticationOTP.setName("Gauurav Dingh");
            dbAuthenticationOTP.setOtp("5678");
            dbAuthenticationOTP.setEmailId("gaurab@turbofinn.com");
            dbAuthenticationOTP.save();
            return new SendOtpOutput(new Response(999,"Error3"));
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
