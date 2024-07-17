package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Order;
import org.turbofinn.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class GetOrderdItemsList implements RequestHandler<GetOrderdItemsList.GetOrderdItemsListInput,GetOrderdItemsList.GetOrderdItemsListOutPut> {
    public static void main(String[] args) {
        GetOrderdItemsListInput input = new GetOrderdItemsListInput();
        input.setUserId("123456user");
        input.setRestaurantId("restaurantId456");
        input.setOrderId("4");
        input.setPaymentStatus("not_paid");  // or "paid"
        System.out.println(new Gson().toJson(new GetOrderdItemsList().handleRequest(input,null)));

    }

    @Override
    public GetOrderdItemsList.GetOrderdItemsListOutPut handleRequest(GetOrderdItemsList.GetOrderdItemsListInput input, Context context) {
        if(input==null || input.orderId==null || input.restaurantId==null || input.userId==null){
            return new GetOrderdItemsList.GetOrderdItemsListOutPut(new GetOrderdItemsList.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }


        if ("not_paid".equalsIgnoreCase(input.getPaymentStatus())) {
            return fetchOrderedList(input.orderId);
        }
         else {
            return fetchAllOrderedList(input.userId,input.restaurantId);
        }

    }

    private GetOrderdItemsListOutPut fetchAllOrderedList(String userId,String restaurantId) {
        List<DB_Order> dbOrders = DB_Order.fetchOrdersByUserID(userId);
//        if (restaurantId != null) {
//            dbOrders = dbOrders.stream()
//                    .filter(x -> x.getRestaurantId() != null && x.getRestaurantId().equalsIgnoreCase(restaurantId))
//                    .toList();
//        }
        List<DB_Order.OrderList> allOrderLists = new ArrayList<>();
        for (DB_Order order : dbOrders) {
            if (order.getOrderLists() != null) {
                allOrderLists.addAll(order.getOrderLists());
            }
        }
        return new GetOrderdItemsList.GetOrderdItemsListOutPut(new GetOrderdItemsList.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),allOrderLists);

    }

    private GetOrderdItemsListOutPut fetchOrderedList(String orderId) {
        DB_Order dbOrder = DB_Order.fetchOrderByOrderID(orderId);
        if(dbOrder==null){
            return new GetOrderdItemsList.GetOrderdItemsListOutPut(new GetOrderdItemsList.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }
        return new GetOrderdItemsList.GetOrderdItemsListOutPut(new GetOrderdItemsList.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),dbOrder.getOrderLists());
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class GetOrderdItemsListOutPut {
        public Response response;
        public List<DB_Order.OrderList> orderList;

    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetOrderdItemsListInput {
        public String orderId;
        public String restaurantId;
        public String userId;
        public String paymentStatus;
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class Response{
        int responseCode;
        String message;

    }
}
