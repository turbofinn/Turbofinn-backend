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

import java.util.List;

public class GetOrderdItemsList implements RequestHandler<GetOrderdItemsList.GetOrderdItemsListInput,GetOrderdItemsList.GetOrderdItemsListOutPut> {
    public static void main(String[] args) {
        GetOrderdItemsListInput input = new GetOrderdItemsListInput();
        input.setUserId("userId789");
        input.setRestaurantId("restaurantId456");
        input.setOrderId("div1002");
        System.out.println(new Gson().toJson(new GetOrderdItemsList().handleRequest(input,null)));

    }

    @Override
    public GetOrderdItemsList.GetOrderdItemsListOutPut handleRequest(GetOrderdItemsList.GetOrderdItemsListInput input, Context context) {
        if(input==null || input.orderId==null || input.restaurantId==null || input.userId==null){
            return new GetOrderdItemsList.GetOrderdItemsListOutPut(new GetOrderdItemsList.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }
        DB_Order dbOrder = DB_Order.fetchItemByID(input.orderId);
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
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class Response{
        int responseCode;
        String message;

    }
}
