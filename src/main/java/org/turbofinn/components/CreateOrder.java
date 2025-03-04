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
import org.turbofinn.dbmappers.DB_User;
import org.turbofinn.enums.OrderSource;
import org.turbofinn.enums.OrderStatus;
import org.turbofinn.util.Constants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateOrder implements RequestHandler<CreateOrder.CreateOrderInput,CreateOrder.CreateOrdersOutput> {

    public static void main(String[] args) {
        String request = "{\n" +
                "    \"mobileNo\": \"6\",\n" +
                "    \"userName\": \"Saurabh\",\n" +
                "    \"restaurantId\": \"04d68d60-4887-4b52-839d-3f2b2a9d4f8a\",\n" +
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
                "    \"customerRating\": 4.5,\n" +
                "    \"orderSource\":\"Manual\"\n" +
                "}";
//        String request = "{\n" +
//                "    \"orderId\": \"79fd6ffd-3236-482f-9e76-e38b81aca8b1\",\n" +
//                "    \"action\": \"UPDATE\",\n" +
//                "    \"orderStatus\": \"DELIVERED\"\n" +
//                "}";

        System.out.println(new Gson().toJson(new CreateOrder().handleRequest(new Gson().fromJson(request, CreateOrderInput.class), null)));

    }

    @Override
    public CreateOrder.CreateOrdersOutput handleRequest(CreateOrder.CreateOrderInput createOrderInput, Context context) {
        System.out.println("input " +new Gson().toJson(createOrderInput));
        if(createOrderInput == null ){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }

        if(createOrderInput.action == null){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
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
        if(createOrderInput.orderLists.isEmpty()){
            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Please add items to order"), null);
        }
        if(createOrderInput.restaurantId == null){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Restaurant id is not provided"), null);
        }
        if(createOrderInput.userId == null && !OrderSource.MANUAL.toString().equals(createOrderInput.getOrderSource().toUpperCase())){
            return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"User id is not provided"), null);
        }

//        if(createOrderInput.tableNo == null){
//            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Please provide table no"), null);
//        }
        if(createOrderInput.paymentStatus == null){
            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Payment status is not provided"), null);
        }
        if(createOrderInput.orderStatus == null){
            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Payment status is not provided"), null);
        }
        DB_Order dbOrder = new DB_Order();
        dbOrder.setRestaurantId(createOrderInput.getRestaurantId());
        dbOrder.setTableNo(createOrderInput.getTableNo()!=null ? createOrderInput.getTableNo():"");

        dbOrder.setTotalAmount(createOrderInput.getTotalAmount());

        List<DB_Order.OrderList> orderLists = new Gson().fromJson(new Gson().toJson(createOrderInput.getOrderLists()), new TypeToken<ArrayList<DB_Order.OrderList>>() {
        }.getType());
        dbOrder.setOrderLists(new Gson().toJson(orderLists));

        dbOrder.setOrderStatus(OrderStatus.PENDING.toString());
        dbOrder.setCustomerRequest(createOrderInput.getCustomerRequest());
        dbOrder.setCustomerFeedback(createOrderInput.getCustomerFeedback());
        dbOrder.setCustomerRating(createOrderInput.getCustomerRating());
        dbOrder.setPaymentStatus(createOrderInput.getPaymentStatus());
        dbOrder.setOrderDate(LocalDate.now().toString());
        try {
            dbOrder.setOrderSource(OrderSource.valueOf(createOrderInput.getOrderSource().toUpperCase()).toString());
        } catch (IllegalArgumentException e) {
            return new CreateOrdersOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "Invalid order source"), null);
        }

        if(OrderSource.MANUAL.toString().equals(createOrderInput.getOrderSource().toUpperCase())){
            if(createOrderInput.mobileNo==null || createOrderInput.mobileNo.isBlank()){
                return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Mobile no.is not provided"), null);
            }
            if(createOrderInput.userName==null || createOrderInput.userName.isBlank()){
                return new CreateOrdersOutput( new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"username is not provided"), null);
            }
            DB_User user = DB_User.fetchUserByMobileNo(createOrderInput.mobileNo);
            if(user!=null){
                dbOrder.setUserId(user.getUserId());
            }else{
                DB_User dbUser = new DB_User();
                dbUser.setMobileNo(createOrderInput.mobileNo);
                dbUser.setUserName(createOrderInput.userName);
                dbUser.save();
                dbOrder.setUserId(dbUser.getUserId());
            }
            dbOrder.setUserName(createOrderInput.userName);
        }
        else{
            dbOrder.setUserId(createOrderInput.getUserId());
        }
        dbOrder.save();
        String order = dbOrder.getOrderId();
        System.out.println(order);
        return new CreateOrdersOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), order);
    }

    public CreateOrder.CreateOrdersOutput updateOrder(CreateOrder.CreateOrderInput createOrderInput){
        DB_Order dbOrder = DB_Order.fetchOrderByOrderID(createOrderInput.orderId);

        if(dbOrder==null){
            return new CreateOrdersOutput( new Response(Constants.GENERIC_RESPONSE_CODE,"Provide correct restaurant id"), null);
        }

        if(createOrderInput.getOrderLists()!=null){
            List<DB_Order.OrderList> orderLists = new Gson().fromJson(createOrderInput.getOrderLists().toString(), new TypeToken<ArrayList<DB_Order.OrderList>>() {
            }.getType());
            dbOrder.setOrderLists(new Gson().toJson(orderLists));
        }
        if(createOrderInput.orderStatus!=null){
            dbOrder.setOrderStatus(createOrderInput.orderStatus.toString());
        }

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
        String orderSource;
        String mobileNo;
        String userName;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }

}

