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

public class GetOrderedItemsList implements RequestHandler<GetOrderedItemsList.GetOrderdItemsListInput, GetOrderedItemsList.GetOrderdItemsListOutPut> {
    public static void main(String[] args) {
        GetOrderdItemsListInput input = new GetOrderdItemsListInput();
        input.setUserId("userId789");
        input.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
        input.setOrderId("orderI535366373");
        input.setPaymentStatus("NOTPAID");  // or "paid"
        System.out.println(new Gson().toJson(new GetOrderedItemsList().handleRequest(input,null)));

    }

    @Override
    public GetOrderedItemsList.GetOrderdItemsListOutPut handleRequest(GetOrderedItemsList.GetOrderdItemsListInput input, Context context) {
        if(input==null || input.restaurantId==null || input.userId==null){
            return new GetOrderedItemsList.GetOrderdItemsListOutPut(new GetOrderedItemsList.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }


        switch (DB_Order.ActionType.getActionType(input.paymentStatus)){
            case NOTPAID:
                return fetchOrderedList(input.orderId);
            case PAID:
                return fetchAllOrderedList(input.userId,input.restaurantId);
            default:
                return new GetOrderedItemsList.GetOrderdItemsListOutPut(new GetOrderedItemsList.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }


    }

    private GetOrderdItemsListOutPut fetchAllOrderedList(String userId, String restaurantId) {
        List<DB_Order> dbOrders = DB_Order.fetchAllItemsByUserId(userId);
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
        if(orderId==null ){
            return new GetOrderdItemsListOutPut(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }
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
