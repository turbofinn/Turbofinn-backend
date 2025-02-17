package org.turbofinn.components;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.*;
import org.turbofinn.dbmappers.DB_Items;
import org.turbofinn.dbmappers.DB_Order;
import org.turbofinn.dbmappers.DB_User;
import org.turbofinn.util.Constants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.stream.Collectors;

public class FetchActiveOrders  implements RequestHandler<FetchActiveOrders.Input,FetchActiveOrders.Output> {


    public static void main(String[] args) {
        Input input = new Input("04d68d60-4887-4b52-839d-3f2b2a9d4f8a");
        System.out.println(new Gson().toJson(new FetchActiveOrders().handleRequest(input,null)));
    }
    @Override
    public FetchActiveOrders.Output handleRequest(FetchActiveOrders.Input input, Context context) {
        if(input==null || input.restaurantId==null){

            return new Output(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }

        List<DB_Order> orders = null;
        List<Order>  orderList = new ArrayList<>();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try{
            orders = DB_Order.fetchAllActiveOrdersByRestaurantId(input.getRestaurantId(),todayDate);

            if(orders != null ){
                for(DB_Order order : orders){
                    if(order.getUserId()==null){
                        return new Output(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Not a valid order"),null);
                    }
                    DB_User user = DB_User.fetchUserByUserID(order.getUserId());
                    Order order1 = new Order();
                    order1.setTableNo(order.getTableNo() != null ? order.getTableNo() : "");
                    order1.setRestaurantId(order.getRestaurantId() != null ? order.getRestaurantId() : "");
                    order1.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus() : "");
                    order1.setTotalAmount(order.getTotalAmount());
                    order1.setOrderStatus(order.getOrderStatus() != null ? order.getOrderStatus() : "");
                    order1.setCustomerRequest(order.getCustomerRequest() != null ? order.getCustomerRequest() : "");
                    order1.setCustomerFeedback(order.getCustomerFeedback() != null ? order.getCustomerFeedback() : "");
                    order1.setCustomerRating(order.getCustomerRating());
                    order1.setOrderDate(order.getOrderDate() != null ? order.getOrderDate() : "");
                    order1.setOrderSource(order.getOrderSource() != null ? order.getOrderSource() : "");
                    order1.setUserName(user != null && user.getUserName() != null ? user.getUserName() : "Unknown");



                    List<Item> items = new Gson().fromJson(order.getOrderLists(), new TypeToken<List<Item>>() {}.getType());
                    List<ItemList> itemLists = items.stream()
                            .map(item -> {
                                DB_Items dbItem = DB_Items.fetchItemByID(item.getItemId());
                                if (dbItem == null) {

                                    System.out.println("Warning: Item with ID " + item.getItemId() + " not found in DB.");
                                    return new ItemList("Unknown Item", item.getQuantity(), 0.0, ""); // Default values
                                }
                                return new ItemList(
                                        dbItem.getName(),
                                        item.getQuantity(),
                                        dbItem.getPrice() * item.getQuantity(),
                                        dbItem.getItemPicture()
                                );
                            })
                            .collect(Collectors.toList());

                    order1.setOrderList(itemLists);
                    orderList.add(order1);


                }
            }else{
                return new Output(new Response(Constants.SUCCESS_RESPONSE_CODE,"No Order found today"),orderList);
            }
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
        private List<Order> orderList;



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

    @Getter@Setter@AllArgsConstructor@NoArgsConstructor
    private static class Order{

        String tableNo;
        String restaurantId;
        String paymentStatus;
        double totalAmount;

        List<ItemList> orderList;
        String orderStatus;
        String customerRequest;
        String customerFeedback;
        double customerRating;
        String orderDate;
        String orderSource;
        String userName;
    }

    @Getter@Setter@AllArgsConstructor@NoArgsConstructor
    private static class Item{
        private String itemId;
        private int quantity;
    }
    @Getter@Setter@AllArgsConstructor@NoArgsConstructor
    private static class ItemList{
        private String itemName;
        private int quantity;
        private double totalPrice;
        private String itemPicture;
    }

}
