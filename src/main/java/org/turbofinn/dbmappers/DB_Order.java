package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;

import java.util.ArrayList;

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
    double totalAmount;
    ArrayList<OrderList> orderLists ;
    String orderStatus;
    String customerRequest;
    String customerFeedbck;
    double customerRating;

    private class OrderList {
        String itemId;
        int quantity;
    }


    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Oredr Saved *** " + new Gson().toJson(this));
    }
}