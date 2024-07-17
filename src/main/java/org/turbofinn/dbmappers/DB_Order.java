package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;
import org.turbofinn.components.OrderListConverter;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static enum ActionType {
        CREATE("CREATE"),
        UPDATE("UPDATE");
        private String text;

        private ActionType(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public static ActionType getActionType(String type) {
            if (type == null) {
                return null;
            }
            switch (type) {
                case "CREATE":
                    return ActionType.CREATE;
                case "UPDATE":
                    return ActionType.UPDATE;
                default:
                    return null;
            }
        }
    }

    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Oredr Saved *** " + new Gson().toJson(this));
    }



    public static List<DB_Order> fetchOrdersByUserID(String userId) {
        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":userId", new AttributeValue().withS(userId));
        DynamoDBQueryExpression<DB_Order> queryExpression = new DynamoDBQueryExpression<DB_Order>()
                .withIndexName("userId-index")
                .withKeyConditionExpression("userId = :userId")
                .withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);
        return AWSCredentials.dynamoDBMapper().query(DB_Order.class, queryExpression);
    }

    public static DB_Order fetchOrderByOrderID(String orderId){
        return (orderId == null) ? null : AWSCredentials.dynamoDBMapper().load(DB_Order.class, orderId);
    }
}