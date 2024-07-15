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
        CreateOrderInput createOrderInput = new CreateOrderInput();
//        createItemsInput.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
//        createItemsInput.setName("paneer chili dusre vale ka");
//        createItemsInput.setType("noodels");
//        createItemsInput.setCuisine("chinese");
//        createItemsInput.setCategory("paneer");
//        createItemsInput.setFlag("veg");
//        createItemsInput.setTag("dineIn");
//        createItemsInput.setDescription("very delicious ,somkey hot ,korean noodles");
//        createItemsInput.setPrice(150.0);
//        createItemsInput.setEta("dineIn");
//        createItemsInput.setItemPicture("dineIn");
//        System.out.println(new Gson().toJson(new CreateItems().handleRequest(createOdersOutput,null)));
    }

    @Override
    public CreateOrder.CreateOrdersOutput handleRequest(CreateOrder.CreateOrderInput createOrderInput, Context context) {
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
        dbOrder.setCustomerFeedbck(createOrderInput.getCustomerFeedbck());
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
        double totalAmount;
        ArrayList<DB_Order.OrderList> orderLists ;
        String orderStatus;
        String customerRequest;
        String customerFeedbck;
        double customerRating;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }

}

