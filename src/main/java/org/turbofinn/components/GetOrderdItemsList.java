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

public class GetOrderdItemsList implements RequestHandler<GetOrderdItemsList.GetOrderdItemsListInput,GetOrderdItemsList.GetOrderdItemsListOutPut> {
    public static void main(String[] args) {
        GetOrderdItemsListInput input = new GetOrderdItemsListInput();
        input.setUserId("123456user");
        input.setRestaurantId("restaurantId456");
        input.setOrderId("4");
        input.setPaymentStatus("paid");  // or "paid"
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

    private GetOrderdItemsListOutPut fetchAllOrderedList(String userId, String restaurantId) {
        List<DB_Order> dbOrders = DB_Order.fetchOrdersByUserID(userId);
        List<OrderedItemDetails> allOrderDetails = new ArrayList<>();
        for (DB_Order order : dbOrders) {
            if (order.getOrderLists() != null) {
                for (DB_Order.OrderList orderList : order.getOrderLists()) {
                    DB_Items item = DB_Items.fetchItemByID(orderList.getItemId());
                    if (item != null) {
                        allOrderDetails.add(new OrderedItemDetails(item, orderList.getQuantity()));
                    }
                }
            }
        }
        return new GetOrderdItemsListOutPut(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), allOrderDetails);
    }
    private GetOrderdItemsListOutPut fetchOrderedList(String orderId) {
        DB_Order dbOrder = DB_Order.fetchOrderByOrderID(orderId);
        if (dbOrder == null) {
            return new GetOrderdItemsListOutPut(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        List<OrderedItemDetails> orderedItemDetails = new ArrayList<>();
        for (DB_Order.OrderList orderList : dbOrder.getOrderLists()) {
            DB_Items item = DB_Items.fetchItemByID(orderList.getItemId());
            if (item != null) {
                orderedItemDetails.add(new OrderedItemDetails(item, orderList.getQuantity()));
            }
        }
        return new GetOrderdItemsListOutPut(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), orderedItemDetails);
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class GetOrderdItemsListOutPut {
        public Response response;
        public List<OrderedItemDetails> orderList;
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
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderedItemDetails {
        public DB_Items item;
        public int quantity;
    }
}
