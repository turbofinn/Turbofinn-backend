package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Order;
import org.turbofinn.enums.OrderStatus;
import org.turbofinn.util.Constants;


public class OrderStatusHandler implements RequestHandler<OrderStatusHandler.input, OrderStatusHandler.output> {


    @Override
    public OrderStatusHandler.output handleRequest(OrderStatusHandler.input input, Context context) {

        if(input == null || input.restaurantId==null|| input.orderId==null){
            return new output(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Please input valid restaurantId and orderId"));
        }
        DB_Order dbOrder= DB_Order.fetchOrderByOrderID(input.orderId);
        if(dbOrder==null){
            return new output(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Order Id not found"));
        }
        if(input.orderStatus.equalsIgnoreCase(OrderStatus.CANCEL.toString())){
            dbOrder.setOrderStatus(OrderStatus.CANCEL.toString());
        } else if (input.orderStatus.equalsIgnoreCase(OrderStatus.DELIVERED.toString())) {
            dbOrder.setOrderStatus(OrderStatus.DELIVERED.toString());
        } else if (input.orderStatus.equalsIgnoreCase(OrderStatus.PREPARED.toString())) {
            dbOrder.setOrderStatus(OrderStatus.PREPARED.toString());
        }
        dbOrder.save();
        return new output(new Response(Constants.SUCCESS_RESPONSE_CODE,"status updated successfully"));


    }


    @Getter@Setter
    @AllArgsConstructor@NoArgsConstructor
    public static class output {
        public Response response;
    }
    @Getter@Setter
    @AllArgsConstructor@NoArgsConstructor
    public static class Response{
        int responseCode;
        String message;
    }
    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class input {
        public String restaurantId;
        public String orderId;
        public String orderStatus;
    }
}
