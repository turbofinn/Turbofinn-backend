package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_AuthenticationOTP;
import org.turbofinn.dbmappers.DB_Table;
import org.turbofinn.dbmappers.DB_User;
import org.turbofinn.util.Constants;

public class VerifyOTP implements RequestHandler<VerifyOTP.VerifyOtpInput,VerifyOTP.VerifyOtpOutput> {
    public static void main(String[] args) {
        VerifyOtpInput input = new Gson().fromJson("{\n" +
                "    \"restaurantId\": \"04d68d60-4887-4b52-839d-3f2b2a9d4f8a\",\n" +
                "    \"tableNo\": \"4\",\n" +
                "    \"mobileNo\": \"8960880615\",\n" +
                "    \"userName\": \"Gaurav Singh\",\n" +
                "    \"otp\": \"1234\"\n" +
                "}", VerifyOtpInput.class);
        System.out.println(new Gson().toJson(new VerifyOTP().handleRequest(input,null)));

    }
    @Override
    public VerifyOTP.VerifyOtpOutput handleRequest(VerifyOTP.VerifyOtpInput input, Context context) {
        if(input==null || input.getOtp()==null || input.getMobileNo()==null){
            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);

        }
        if(input.tableNo==null || input.getRestaurantId()==null){
            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Please give valid table no. and restaurant id"),null);

        }

        DB_AuthenticationOTP otp = DB_AuthenticationOTP.fetchOtpByMobileNo(input.mobileNo);

        System.out.println(new Gson().toJson(otp));
        if(otp == null ){

            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }
        if(otp.getOtp().equalsIgnoreCase(input.otp)){

            DB_User dbUser = DB_User.fetchUserByMobileNo(input.mobileNo);

            if(dbUser==null){
                DB_User user = new DB_User();
                user.setMobileNo(input.mobileNo);
                user.setUserName(input.userName);
                user.save();

                System.out.println("User is created"+ user.getUserId());

                DB_Table dbTable = DB_Table.fetchByTableNo(input.tableNo, input.restaurantId);
                if(dbTable==null){
                    return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"No table found"),null);
                }
                else{
                    dbTable.setStatus("occupied");
                    dbTable.setUserId(user.getUserId());
                    dbTable.setMobileNo(user.getMobileNo());
                    dbTable.save();
                }

                DB_AuthenticationOTP.deleteOtp(otp);
                return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),user.getUserId());
            }
            DB_Table dbTable = DB_Table.fetchByTableNo(input.tableNo, input.restaurantId);
            if(dbTable==null){
                return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"No table found"),null);
            }
            else{
                dbTable.setStatus("occupied");
                dbTable.setUserId(dbUser.getUserId());
                dbTable.setMobileNo(dbUser.getMobileNo());
                dbTable.save();
            }
            DB_AuthenticationOTP.deleteOtp(otp);

            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), dbUser.getUserId());
        }
        else{

            return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }


//        System.out.println(new Gson().toJson(otp));
//
//        return new VerifyOTP.VerifyOtpOutput(new VerifyOTP.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));

    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class VerifyOtpOutput {
        public Response response;
        public String userId;

    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class VerifyOtpInput {
        public String mobileNo;
        public String otp;
        public String restaurantId;
        public String tableNo;
        public String userName;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
