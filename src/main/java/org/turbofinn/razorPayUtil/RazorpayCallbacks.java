package org.turbofinn.razorPayUtil;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

public class RazorpayCallbacks implements RequestHandler<PaymentAuthenticationModel.RazorPayPaymentUpdateInput, Object>{
    @Override
    public Object handleRequest(PaymentAuthenticationModel.RazorPayPaymentUpdateInput input, Context context) {

        try {
            System.out.println("BODY : " + input.getBody());
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PaymentAuthenticationModel paymentAuthenticationModel = null;
            paymentAuthenticationModel = mapper.readValue(input.getBody(), PaymentAuthenticationModel.class);
            if (paymentAuthenticationModel != null && paymentAuthenticationModel.getEvent()!=null) {
                switch (paymentAuthenticationModel.getEvent()) {
                    case RazorpayConstants.PAYMENT_FAILED:
                    case RazorpayConstants.PAYMENT_CAPTURED:
                    case RazorpayConstants.PAYMENT_AUTHORIZED:
                        System.out.println("Successs");

                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}

