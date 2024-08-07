package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "Feedback")
public class DB_Feedback extends DB_DateTable{
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    String feedbackId;
    String userId;
    String restaurantId;
    String message;
    String rating;
    Date timestamp;

    public static enum ActionType{
        CREATE("CREATE"),
        UPDATE("UPDATE"),
        DELETE("DELETE"),
        FETCH("FETCH");
        private String text;

        private ActionType(String text){
            this.text = text;
        }

        @Override
        public String toString(){
            return this.text;
        }

        public static DB_Feedback.ActionType getActionType(String type){
            if(type == null){
                return null;
            }
            switch (type){
                case "CREATE":
                    return ActionType.CREATE;
                case "UPDATE":
                    return ActionType.UPDATE;
                case "DELETE":
                    return ActionType.DELETE;
                case "FETCH":
                    return ActionType.FETCH;
                default:
                    return null;
            }
        }
    }

    public void initializeTimestamp() {
        this.timestamp = new Date();
    }

    public void save() {
        if (this.timestamp == null) {
            initializeTimestamp();
        }
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Feedback Saved ***" + new Gson().toJson(this));
    }

    public void delete(String feedbackId) {
        AWSCredentials.dynamoDBMapper().delete(feedbackId);
        System.out.println("*** Feedback Deleted ***");
    }

    public static DB_Feedback fetchFeedbackByID(String feedbackId){
        return (feedbackId == null) ? null : AWSCredentials.dynamoDBMapper().load(DB_Feedback.class, feedbackId);
    }

    public static List<DB_Feedback> fetchFeedbackByRestaurantID(String restaurantId) {
        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":restaurantId", new AttributeValue().withS(restaurantId));
        DynamoDBQueryExpression<DB_Feedback> queryExpression = new DynamoDBQueryExpression<DB_Feedback>()
                .withIndexName("restaurantId-index")
                .withKeyConditionExpression("restaurantId = :restaurantId")
                .withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);
        return AWSCredentials.dynamoDBMapper().query(DB_Feedback.class, queryExpression);
    }
}
