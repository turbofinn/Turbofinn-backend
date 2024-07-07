package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "User")
public class DB_User extends DB_DateTable{

    @DynamoDBHashKey
    String userId;
    String userName;
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "mobileNo-index")
    String mobileNo;
    String email;
    String gender;
    String dob;
    String profilePicture;


    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** User Saved *** " + new Gson().toJson(this));
    }

}
