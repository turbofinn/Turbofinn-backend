package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.util.Constants;

public class SendOtp implements RequestHandler<SendOtp.SendOtpInput, SendOtp.SendOtpOutput> {
    @Override
    public SendOtpOutput handleRequest(SendOtp.SendOtpInput input, Context context) {
        if(input==null || input.mobileNo==null){
            return new SendOtpOutput(new Response(999,"Error3"));

        }
        return null;


    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public class SendOtpOutput {
        public Response response;
    }


    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public class SendOtpInput {
        public String mobileNo;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public class Response {
        int responseCode;
        String message;
    }
}
