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
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;

public class FetchActiveOrders  implements RequestHandler<FetchActiveOrders.Input,FetchActiveOrders.Output> {

    public static void main(String[] args) {
        Input input = new Input("04d68d60-4887-4b52-839d-3f2b2a9d4f8a");
        System.out.println(new Gson().toJson(new FetchActiveOrders().handleRequest(input,null)));
    }
    @Override
    public FetchActiveOrders.Output handleRequest(FetchActiveOrders.Input input, Context context) {
        if(input==null && input.restaurantId==null){

            return new Output(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }

        List<DB_Order> orderList = null;
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try{
            orderList = DB_Order.fetchAllActiveOrdersByRestaurantId(input.getRestaurantId(),todayDate);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new Output(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),orderList);

    }



    @Getter@Setter
    @NoArgsConstructor@AllArgsConstructor
    public static class  Output{

        private Response response;
        private List<DB_Order> orderList;


    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class  Input{
        private String restaurantId;


    }

    @Getter@Setter@AllArgsConstructor@NoArgsConstructor
    private static class  Response{
        private int responseCode;
        private String message;

    }
}
