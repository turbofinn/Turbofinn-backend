package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "Ingredients")
public class DB_Ingredients extends DB_DateTable {

    @DynamoDBHashKey
    String itemId;
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "restaurantId")
    String restaurantId;
    @DynamoDBAttribute
    String ingredients;

    @ToString
    @DynamoDBDocument
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ingredients {
        public String id;
        public String name;
        public String quantity;
        public String unit;
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

        public static DB_Ingredients.ActionType getActionType(String type) {
            if (type == null) {
                return null;
            }
            switch (type) {
                case "CREATE":
                    return DB_Ingredients.ActionType.CREATE;
                case "UPDATE":
                    return DB_Ingredients.ActionType.UPDATE;
                default:
                    return null;
            }
        }
    }

    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Ingredients Saved ***" + new Gson().toJson(this));
    }

    public static DB_Ingredients fetchIngredientsByRestaurantIDAndItemId(String restaurantId, String itemId) {
        if (restaurantId == null || itemId == null) {
            throw new IllegalArgumentException("restaurantId or itemId cannot be null");
        }

        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":restaurantId", new AttributeValue().withS(restaurantId));
        expressionAttributeValues.put(":itemId", new AttributeValue().withS(itemId));

        DynamoDBQueryExpression<DB_Ingredients> queryExpression = new DynamoDBQueryExpression<DB_Ingredients>()
                .withIndexName("restaurantId-itemId-index")
                .withKeyConditionExpression("restaurantId = :restaurantId and itemId = :itemId")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);

        List<DB_Ingredients> result = AWSCredentials.dynamoDBMapper().query(DB_Ingredients.class, queryExpression);
        return result.isEmpty() ? null : result.get(0);
    }


}
