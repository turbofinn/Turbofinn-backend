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

public class VerifyOTP implements RequestHandler<VerifyOTP.VerifyOtpInput,VerifyOTP.VerifyOtpOutput> {
    public static void main(String[] args) {
        VerifyOtpInput input = new VerifyOtpInput();
        input.setMobileNo("7985157933");
        input.setOtp("1853");
        System.out.println(new Gson().toJson(new VerifyOTP().handleRequest(input,null)));

    }
    @Override
    public VerifyOTP.VerifyOtpOutput handleRequest(VerifyOTP.VerifyOtpInput input, Context context) {
        if(input==null || input.getOtp()==null || input.getMobileNo()==null){
            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));

        }

        DB_AuthenticationOTP otp = DB_AuthenticationOTP.fetchOtpByMobileNo(input.mobileNo);

        System.out.println(new Gson().toJson(otp));
        if(otp == null ){

            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        if(otp.getOtp().equalsIgnoreCase(input.otp)){

            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
        }
        else{

            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }


//        System.out.println(new Gson().toJson(otp));
//
//        return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));

    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class VerifyOtpOutput {
        public Response response;

    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class VerifyOtpInput {
        public String mobileNo;
        public String otp;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
