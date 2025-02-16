package org.turbofinn.dbmappers;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "Payments")
public class DB_Payments extends DB_DateTable{

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    String paymentId;
    String restaurantId;
    String orderId;
    String tableNo;
    String paymentStatus;
    double paymentAmount;
    String paymentDate;
    String userId;
    String paymentMode;
    String invoiceUrl;

    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Payment Saved *** " + new Gson().toJson(this));
    }

    public static DB_Payments fetchPaymentByID(String paymentId){
        return (paymentId == null) ? null : AWSCredentials.dynamoDBMapper().load(DB_Payments.class, paymentId);
    }

    public static List<DB_Payments> fetchPaymentsByRestaurantID(String restaurantId) {
        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":restaurantId", new AttributeValue().withS(restaurantId));
        DynamoDBQueryExpression<DB_Payments> queryExpression = new DynamoDBQueryExpression<DB_Payments>()
                .withIndexName("restaurantId-index")
                .withKeyConditionExpression("restaurantId = :restaurantId")
                .withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);
        return AWSCredentials.dynamoDBMapper().query(DB_Payments.class, queryExpression);
    }



    public static DB_Payments fetchPaymentsByOrderID(String orderId) {
        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":orderId", new AttributeValue().withS(orderId));
        DynamoDBQueryExpression<DB_Payments> queryExpression = new DynamoDBQueryExpression<DB_Payments>()
                .withIndexName("orderId-index")
                .withKeyConditionExpression("orderId = :orderId")
                .withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);
        PaginatedQueryList<DB_Payments> dbQueryList = AWSCredentials.dynamoDBMapper().query(DB_Payments.class,
                queryExpression);
        return (dbQueryList != null && dbQueryList.size() > 0) ? dbQueryList.get(0) : null;
    }

    public static enum PaymentStatus {
        CREATED("Created"),
        PAID("Paid"),
        SUCCESS("Success"),
        PENDING("Pending"),
        FAILED("Failed");
        private String text;

        private PaymentStatus(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public static PaymentStatus getPaymentStatus(String type) {
            if (type == null) {
                return null;
            }
            switch (type) {
                case "CREATED":
                    return PaymentStatus.CREATED;
                case "SUCCESS":
                    return PaymentStatus.SUCCESS;
                case "PAID":
                    return PaymentStatus.PAID;
                case "PENDING":
                    return PaymentStatus.PENDING;
                case "FAILED":
                    return PaymentStatus.FAILED;
                default:
                    return null;
            }
        }

    }
}
