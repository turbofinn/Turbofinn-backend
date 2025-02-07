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
@DynamoDBTable(tableName = "User")
public class DB_User extends DB_DateTable{

    public static void main(String[] args) {
        DB_User dbUser = new DB_User();
        dbUser.setUserName("Divyanshi");
        dbUser.setEmail("div@gmail.com");
        dbUser.setMobileNo("67687776758");
        dbUser.setDob("2024-01-01");
        dbUser.setProfilePicture("sdfgfb.jpeg");
        dbUser.setGender("Female");
        dbUser.save();
    }

    @DynamoDBAutoGeneratedKey
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

    public static DB_User fetchUserByUserID(String userId){
        return (userId == null) ? null : AWSCredentials.dynamoDBMapper().load(DB_User.class, userId);

    }

    public static DB_User fetchUserByMobileNo(String mobileNo) {
        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":mobileNo", new AttributeValue().withS(mobileNo));
        DynamoDBQueryExpression<DB_User> queryExpression = new DynamoDBQueryExpression<DB_User>()
                .withIndexName("mobileNo-index")
                .withKeyConditionExpression("mobileNo = :mobileNo")
                .withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);
        PaginatedQueryList<DB_User> dbQueryList = AWSCredentials.dynamoDBMapper().query(DB_User.class,
                queryExpression);
        return (dbQueryList != null && dbQueryList.size() > 0) ? dbQueryList.get(0) : null;
    }

}
