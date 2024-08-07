package org.turbofinn.razorPayUtil;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import com.razorpay.*;
import org.json.JSONObject;
import org.turbofinn.dbmappers.DB_Payments;
import org.turbofinn.util.Constants;

import java.time.LocalDate;

public class CreatePaymentOrder implements RequestHandler<CreatePaymentOrder.CreatePaymentOrderInput,CreatePaymentOrder.CreatePaymentOrderOutput> {

    public static void main(String[] args) {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", 50000); // amount in the smallest currency unit (e.g., paise)
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt#1");
        orderRequest.put("payment_capture", 1);
        System.out.println(new Gson().toJson(new CreatePaymentOrder().handleRequest(new Gson().fromJson(String.valueOf(orderRequest),CreatePaymentOrderInput.class),null)));
    }

    @Override
    public CreatePaymentOrder.CreatePaymentOrderOutput handleRequest(CreatePaymentOrder.CreatePaymentOrderInput input, Context context) {
        System.out.println("Input payment" + new Gson().toJson(input));
        if(input == null){
            return new CreatePaymentOrderOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        if(StringUtils.isAnyBlank(input.restaurantID,input.userID,input.tableNo)){
            return new CreatePaymentOrderOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        if(input.amount<=0){
            return new CreatePaymentOrderOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Payment amount cannot be less then 1 Rs."));
        }

        RazorpayClient razorpay = null;
        try {
            razorpay = new RazorpayClient("rzp_test_CpaKZGQeJCUIbQ", "1qYbYIePiuBR5YDanCyfwqJy");
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (input.amount*100)); // In paisa
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt#1");
            orderRequest.put("payment_capture", 1);
            Order order = razorpay.Orders.create(orderRequest);

            System.out.println("Order created successfully!");
            System.out.println("Order ID: " + order.get("id"));
            System.out.println("Order Status: " + order.get("status"));
            System.out.println("Order Amount: " + order.get("amount"));


            if(order.get("status").toString().equalsIgnoreCase(DB_Payments.PaymentStatus.CREATED.toString())){
                new DB_Payments(null,input.restaurantID,order.get("id"),input.tableNo, DB_Payments.PaymentStatus.PENDING.toString(), input.amount, LocalDate.now().toString(),input.userID).save();
                double amount = Double.parseDouble(order.get("amount").toString())/100;
                return new CreatePaymentOrderOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),order.get("id"),amount,order.get("status"));
            }

        } catch (RazorpayException e) {
            return new CreatePaymentOrderOutput(new Response(Constants.GENERIC_RESPONSE_CODE,Constants.GENERIC_ERROR_RESPONSE_MESSAGE));
        }
        return new CreatePaymentOrderOutput(new Response(Constants.GENERIC_RESPONSE_CODE,Constants.GENERIC_ERROR_RESPONSE_MESSAGE));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor

    public static class CreatePaymentOrderInput {
        public double amount;
        public String currency;
        public String receipt;
        public String restaurantID;
        public String userID;
        public String tableNo;
        public int payment_capture;
    }

    @Getter@Setter@AllArgsConstructor
    public static class CreatePaymentOrderOutput {
        public Response response;
        public String orderID;
        public double amount;
        public String paymentStatus;

        public CreatePaymentOrderOutput(Response response) {
            this.response = response;
        }
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }
}
