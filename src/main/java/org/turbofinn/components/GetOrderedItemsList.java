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
import java.util.stream.Collectors;

public class GetOrderedItemsList implements RequestHandler<GetOrderedItemsList.GetOrderdItemsListInput, GetOrderedItemsList.GetOrderdItemsListOutPut> {
    public static void main(String[] args) {
        GetOrderdItemsListInput input = new GetOrderdItemsListInput();
        input.setUserId("userId789");
        input.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
        input.setOrderIds(List.of("2a72ea99-a2d0-4175-9c6a-fbc60e08ba84", "98546835-c4c4-490c-9a21-44faadbef6a5"));
        input.setPaymentStatus("NOTPAID");  // or "paid"
        System.out.println(new Gson().toJson(new GetOrderedItemsList().handleRequest(input,null)));

    }

    @Override
    public GetOrderedItemsList.GetOrderdItemsListOutPut handleRequest(GetOrderedItemsList.GetOrderdItemsListInput input, Context context) {
        if(input==null || input.restaurantId==null || input.userId==null){
            return new GetOrderedItemsList.GetOrderdItemsListOutPut(new GetOrderedItemsList.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null,0);
        }


        switch (DB_Order.ActionType.getActionType(input.paymentStatus)){
            case NOTPAID:
                return fetchOrderedList(input.orderIds);
            case PAID:
                return fetchAllOrderedList(input.userId,input.restaurantId);
            default:
                return new GetOrderedItemsList.GetOrderdItemsListOutPut(new GetOrderedItemsList.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null,0);
        }


    }

    private GetOrderdItemsListOutPut fetchAllOrderedList(String userId, String restaurantId) {
        List<DB_Order> dbOrders = DB_Order.fetchAllItemsByUserId(userId);
        List<OrderedItemDetails> allOrderDetails = new ArrayList<>();
        for (DB_Order order : dbOrders) {
            if (order.getOrderLists() != null && "PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                for (DB_Order.OrderList orderList : order.getOrderLists()) {
                    DB_Items item = DB_Items.fetchItemByID(orderList.getItemId());
                    if (item != null) {
                        allOrderDetails.add(new OrderedItemDetails(item, orderList.getQuantity(),item.getPrice()*orderList.getQuantity()));
                    }
                }
            }
        }
        return new GetOrderdItemsListOutPut(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), allOrderDetails,0);
    }
    private GetOrderdItemsListOutPut fetchOrderedList(List<String> orderId) {
        if(orderId==null ){
            return new GetOrderdItemsListOutPut(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null,0);
        }
        List<DB_Order> dbOrders = new ArrayList<>();
        dbOrders = orderId.stream()
                .map(DB_Order::fetchOrderByOrderID)
                .filter(dbOrder -> dbOrder != null && "NOTPAID".equalsIgnoreCase(dbOrder.getPaymentStatus()))
                .collect(Collectors.toList());
        if (dbOrders == null) {
            return new GetOrderdItemsListOutPut(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null,0);
        }
        List<OrderedItemDetails> orderedItemDetails = new ArrayList<>();
        double totalAmount =0;
        for (DB_Order order : dbOrders) {
            if (order.getOrderLists() != null) {
                for (DB_Order.OrderList orderList : order.getOrderLists()) {
                    DB_Items item = DB_Items.fetchItemByID(orderList.getItemId());
                    if (item != null) {
                        orderedItemDetails.add(new OrderedItemDetails(item, orderList.getQuantity(), item.getPrice()*orderList.getQuantity()));
                        totalAmount+=item.getPrice()*orderList.getQuantity();
                    }
                }
            }
        }
        return new GetOrderdItemsListOutPut(new Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), orderedItemDetails,totalAmount);
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class GetOrderdItemsListOutPut {
        public Response response;
        public List<OrderedItemDetails> orderList;
        double totalAmount;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetOrderdItemsListInput {
        public List<String> orderIds;
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
        public double price;
    }
}
