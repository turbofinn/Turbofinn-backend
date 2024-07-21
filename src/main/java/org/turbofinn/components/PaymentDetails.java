package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Payments;
import org.turbofinn.util.Constants;

public class PaymentDetails implements RequestHandler<PaymentDetails.CreatePaymentsInput, PaymentDetails.CreatePaymentsOutput> {

    public static void main(String[] args) {
        PaymentDetails.CreatePaymentsInput input = new PaymentDetails.CreatePaymentsInput();
        input.setRestaurantId("80219b60-0761-4a94-b923-f17353db6574");
        input.setOrderId("vhbjnj");
        input.setTableNo("25");
        input.setPaymentStatus("paid");
        input.setPaymentAmount(6500.00);
        input.setPaymentDate("21/07/2024");
        input.setUserId("bhsbfhs-fdgdf-fdg");
        System.out.println(new Gson().toJson(input));
        System.out.println(new Gson().toJson(new PaymentDetails().handleRequest(input,null)));
    }

    @Override
    public PaymentDetails.CreatePaymentsOutput handleRequest(PaymentDetails.CreatePaymentsInput input, Context context) {
        if(input == null ){
            return new PaymentDetails.CreatePaymentsOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        else if(input.restaurantId == null){
            return new PaymentDetails.CreatePaymentsOutput(new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide RestaurantId"), null);
        }
        else{
            return createNewPayment(input);
        }
    }

    public PaymentDetails.CreatePaymentsOutput createNewPayment(PaymentDetails.CreatePaymentsInput input){
        DB_Payments dbPayments = new DB_Payments();
        dbPayments.setPaymentId(input.getPaymentId());
        dbPayments.setRestaurantId(input.getRestaurantId());
        dbPayments.setOrderId(input.getOrderId());
        dbPayments.setTableNo(input.getTableNo());
        dbPayments.setPaymentStatus(input.getPaymentId());
        dbPayments.setPaymentAmount(input.getPaymentAmount());
        dbPayments.setPaymentDate(dbPayments.getPaymentDate());
        dbPayments.setPaymentId(dbPayments.getUserId());
        dbPayments.save();
        return new PaymentDetails.CreatePaymentsOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), dbPayments);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentsInput {
        String paymentId;
        String restaurantId;
        String orderId;
        String tableNo;
        String paymentStatus;
        Double paymentAmount;
        String paymentDate;
        String userId;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreatePaymentsOutput {
        public PaymentDetails.Response response;
        DB_Payments dbPayments;
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }

}
