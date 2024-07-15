package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;
import org.turbofinn.components.OrderListConverter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "Order")
public class DB_Order extends DB_DateTable {

    @DynamoDBHashKey
    String orderId;
    String tableNo;
    String userId;
    String restaurantId;
    String paymentStatus;
    double totalAmount;

    @DynamoDBTypeConverted(converter = OrderListConverter.class)
    List<OrderList> orderLists;
    String orderStatus;
    String customerRequest;
    String customerFeedback;
    double customerRating;

    public static class OrderList {
        String itemId;
        int quantity;
    }


    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Oredr Saved *** " + new Gson().toJson(this));
    }
}