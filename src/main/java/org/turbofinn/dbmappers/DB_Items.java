package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "Items")
public class DB_Items extends DB_DateTable {

    @DynamoDBHashKey
    String itemId;
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "restaurantId-index")
    String restaurantId;
    String name;

    String type;        //  food , beverage ,bakery
    String cuisine;     // Indian, chinese,japanese
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "category-index")
    String category;    // fastfood, chinese, biryani, pizza ,burger
    String flag;         //veg ,nonveg ,alcoholic,nonalcoholic
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "tag-index")
    String tag;
    String description;
    Double price;
    String eta;
    String itemPicture;


    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Item Saved *** " + new Gson().toJson(this));
    }

    public DB_Items fetchItemByID(String itemId){
        return (itemId == null) ? null : AWSCredentials.dynamoDBMapper().load(DB_Items.class, itemId);

    }

    public static List<DB_Items> fetchItemsByRestaurantID(String restaurantId) {
        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":restaurantId", new AttributeValue().withS(restaurantId));
        DynamoDBQueryExpression<DB_Items> queryExpression = new DynamoDBQueryExpression<DB_Items>()
                .withIndexName("restaurantId-index")
                .withKeyConditionExpression("restaurantId = :restaurantId")
                .withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);
        return AWSCredentials.dynamoDBMapper().query(DB_Items.class, queryExpression);
    }
}