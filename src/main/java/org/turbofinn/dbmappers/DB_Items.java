package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
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
}