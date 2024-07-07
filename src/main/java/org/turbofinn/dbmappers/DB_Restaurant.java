package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "Restaurant")
public class DB_Restaurant extends DB_DateTable{

    @DynamoDBHashKey
    String restaurantId;
    String name;
    String address;
    String city;
    String state;
    String pincode;
    String emailId;
    String contactNo;
    String restaurntAccountNo;


    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Restaurant Saved *** " + new Gson().toJson(this));
    }

}