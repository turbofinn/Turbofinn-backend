package org.turbofinn.components;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Items;
import org.turbofinn.dbmappers.DB_Order;
import org.turbofinn.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CreateOrder implements RequestHandler<CreateOrder.CreateOrderInput,CreateOrder.CreateOrdersOutput> {

    public static void main(String[] args) {
        String request = "{\n" +
                "    \"orderId\": \"div1002\",\n" +
                "    \"tableNo\": \"table5\",\n" +
                "    \"userId\": \"userId789\",\n" +
                "    \"restaurantId\": \"restaurantId456\",\n" +
                "    \"totalAmount\": 35.0,\n" +
                "    \"paymentStatus\": \"paid\",\n" +
                "    \"orderLists\": [\n" +
                "        {\n" +
                "            \"itemId\": \"item1\",\n" +
                "            \"quantity\": 2\n" +
                "        },\n" +
                "        {\n" +
                "            \"itemId\": \"item2\",\n" +
                "            \"quantity\": 1\n" +
                "        }\n" +
                "    ],\n" +
                "    \"orderStatus\": \"orderStatus\",\n" +
                "    \"customerRequest\": \"Please make it spicy\",\n" +
                "    \"customerFeedbck\": \"Great service\",\n" +
                "    \"customerRating\": 4.5\n" +
                "}\n";
        System.out.println(new Gson().toJson(new CreateOrder().handleRequest(new Gson().fromJson(request, CreateOrderInput.class), null)));

    }

    @Override
    public CreateOrder.CreateOrdersOutput handleRequest(CreateOrder.CreateOrderInput createOrderInput, Context context) {
        System.out.println("input " +new Gson().toJson(createOrderInput));
        if(createOrderInput == null ){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        if(createOrderInput.orderLists.isEmpty()){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Please add items to order"));
        }
        //add more checks



        DB_Order dbOrder = new DB_Order();
        dbOrder.setRestaurantId(createOrderInput.getRestaurantId());
        dbOrder.setOrderId(createOrderInput.getOrderId());
        dbOrder.setTableNo(createOrderInput.getTableNo());
        dbOrder.setUserId(createOrderInput.getUserId());
        dbOrder.setTotalAmount(createOrderInput.getTotalAmount());
        dbOrder.setOrderLists(createOrderInput.getOrderLists());
        dbOrder.setOrderStatus(createOrderInput.getOrderStatus());
        dbOrder.setCustomerRequest(createOrderInput.getCustomerRequest());
        dbOrder.setCustomerFeedback(createOrderInput.getCustomerFeedback());
        dbOrder.setCustomerRating(createOrderInput.getCustomerRating());
        dbOrder.save();
        return new CreateOrdersOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateOrdersOutput {
        public Response response;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateOrderInput {
        String orderId;
        String tableNo;
        String userId;
        String restaurantId;
        String paymentStatus;
        double totalAmount;
        ArrayList<DB_Order.OrderList> orderLists ;
        String orderStatus;
        String customerRequest;
        String customerFeedback;
        double customerRating;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }

}

