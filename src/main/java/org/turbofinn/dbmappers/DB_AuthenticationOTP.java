package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "AuthenticationOTP")
public class DB_AuthenticationOTP extends DB_DateTable {
    public static void main(String[] args) {
        DB_AuthenticationOTP dbAuthenticationOTP = new DB_AuthenticationOTP();
        dbAuthenticationOTP.setDeviceId(UUID.randomUUID().toString());
        dbAuthenticationOTP.setMobileNo("8960880615");
        dbAuthenticationOTP.setName("Gauurav Dingh");
        dbAuthenticationOTP.setOtp("5678");
        dbAuthenticationOTP.setEmailId("gaurab@turbofinn.com");
        dbAuthenticationOTP.save();
    }

    @DynamoDBHashKey
    String deviceId;
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "mobileNo-index")
    String mobileNo;
    String name;
    String emailId;
    String otp;

    public void save() {
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** AuthenticationOTP Saved *** " + new Gson().toJson(this));
    }
}
