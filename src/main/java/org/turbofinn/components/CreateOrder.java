package org.turbofinn.components;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Order;
import org.turbofinn.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CreateOrder implements RequestHandler<CreateOrder.CreateOrderInput,CreateOrder.CreateOrdersOutput> {

    public static void main(String[] args) {
        String request = "{\n" +
                "    \"tableNo\": \"table5\",\n" +
                "    \"userId\": \"3b813bb7-719c-4f4c-bf39-61ec1b8846a9\",\n" +
                "    \"restaurantId\": \"308bc44a-de00-488e-b980-5ee0797e82e2\",\n" +
                "    \"totalAmount\": 560,\n" +
                "    \"action\": \"CREATE\",\n" +
                "    \"paymentStatus\": \"NOTPAID\",\n" +
                "    \"orderLists\": [\n" +
                "        {\n" +
                "            \"itemId\": \"9aabe7cd-7c6f-4e5e-b556-12c478e744gh\",\n" +
                "            \"quantity\": 2,\n" +
                "            \"price\": 280\n" +
                "        }\n" +
                "    ],\n" +
                "    \"orderStatus\": \"orderStatus\",\n" +
                "    \"customerRequest\": \"Please make it spicy\",\n" +
                "    \"customerFeedback\": \"Great service\",\n" +
                "    \"customerRating\": 4.5\n" +
                "}";
        System.out.println(new Gson().toJson(new CreateOrder().handleRequest(new Gson().fromJson(request, CreateOrderInput.class), null)));

    }

    @Override
    public CreateOrder.CreateOrdersOutput handleRequest(CreateOrder.CreateOrderInput createOrderInput, Context context) {
        System.out.println("input " +new Gson().toJson(createOrderInput));
        if(createOrderInput == null ){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        if(createOrderInput.orderLists.isEmpty()){
            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Please add items to order"), null);
        }
        if(createOrderInput.action == null){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        if(createOrderInput.restaurantId == null){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Restaurant id is not provided"), null);
        }
        if(createOrderInput.userId == null){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"User id is not provided"), null);
        }
        if(createOrderInput.tableNo == null){
            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide table no"), null);
        }
        if(createOrderInput.paymentStatus == null){
            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Payment status is not provided"), null);
        }
        if(createOrderInput.orderStatus == null){
            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Payment status is not provided"), null);
        }


        switch (DB_Order.ActionType.getActionType(createOrderInput.action)) {
            case CREATE:
                return createNewOrder(createOrderInput);
            case UPDATE:
                return updateOrder(createOrderInput);
            default:
                return new CreateOrdersOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
    }

    public CreateOrder.CreateOrdersOutput createNewOrder(CreateOrder.CreateOrderInput createOrderInput){
        DB_Order dbOrder = new DB_Order();
        dbOrder.setRestaurantId(createOrderInput.getRestaurantId());
        dbOrder.setTableNo(createOrderInput.getTableNo());
        dbOrder.setUserId(createOrderInput.getUserId());
        dbOrder.setTotalAmount(createOrderInput.getTotalAmount());

        List<DB_Order.OrderList> orderLists = new Gson().fromJson(new Gson().toJson(createOrderInput.getOrderLists()), new TypeToken<ArrayList<DB_Order.OrderList>>() {
        }.getType());
        dbOrder.setOrderLists(new Gson().toJson(orderLists));

        dbOrder.setOrderStatus(createOrderInput.getOrderStatus());
        dbOrder.setCustomerRequest(createOrderInput.getCustomerRequest());
        dbOrder.setCustomerFeedback(createOrderInput.getCustomerFeedback());
        dbOrder.setCustomerRating(createOrderInput.getCustomerRating());
        dbOrder.setPaymentStatus(createOrderInput.getPaymentStatus());
        dbOrder.save();
        String order = dbOrder.getOrderId();
        System.out.println(order);
        return new CreateOrdersOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), order);
    }

    public CreateOrder.CreateOrdersOutput updateOrder(CreateOrder.CreateOrderInput createOrderInput){
        DB_Order dbOrder = DB_Order.fetchOrderByOrderID(createOrderInput.orderId);
        List<DB_Order.OrderList> orderLists = new Gson().fromJson(createOrderInput.getOrderLists().toString(), new TypeToken<ArrayList<DB_Order.OrderList>>() {
        }.getType());
        dbOrder.setOrderLists(new Gson().toJson(orderLists));
        dbOrder.save();
        return new CreateOrdersOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), createOrderInput.orderId);
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateOrdersOutput {
        public Response response;
        public String orderId;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateOrderInput {
        String orderId;
        String tableNo;
        String userId;
        String restaurantId;
        String paymentStatus;
        String action;
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

