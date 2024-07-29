package org.turbofinn.razorPayUtil;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.components.CreateItems;
import com.razorpay.*;
import org.json.JSONObject;
import org.turbofinn.util.Constants;

public class CreatePaymentOrder implements RequestHandler<CreatePaymentOrder.CreatePaymentOrderInput,CreatePaymentOrder.CreatePaymentOrderOutput> {

    public static void main(String[] args) {
        try {
            RazorpayClient razorpay = new RazorpayClient("rzp_test_CpaKZGQeJCUIbQ", "1qYbYIePiuBR5YDanCyfwqJy");
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", 50000); // amount in the smallest currency unit (e.g., paise)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt#1");
            orderRequest.put("payment_capture", 1);
            // Create the order
            Order order = razorpay.Orders.create(orderRequest);

            // Print the order ID
            System.out.println("Order ID: " + order.get("id"));
        } catch (RazorpayException e) {
            // Handle the exception
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public CreatePaymentOrder.CreatePaymentOrderOutput handleRequest(CreatePaymentOrder.CreatePaymentOrderInput input, Context context) {
        System.out.println("Input payment" + new Gson().toJson(input));
        if(input == null){

        }
        return null;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor

    public static class CreatePaymentOrderInput {
        public double amount;
        public String currency;
        public String receipt;
        public int payment_capture;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreatePaymentOrderOutput {
        public Response response;
        public String orderID;
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }
}
