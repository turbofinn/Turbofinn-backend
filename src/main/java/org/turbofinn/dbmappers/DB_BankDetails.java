package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;


@Getter@Setter@NoArgsConstructor@AllArgsConstructor@ToString
public class DB_BankDetails extends DB_DateTable{

    @DynamoDBHashKey
    String restaurantId;
    String accountNumber;
    String ifsc;
    String accHolderName;
    String razorpayAccId;
    String email;
    String mobileNumber;


    public void save(){
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Bank Account Details Saved *** " + new Gson().toJson(this));
    }

    public static DB_BankDetails fetchBankDetailsByRestaurantId(String restaurantId){
        return (restaurantId == null) ? null : AWSCredentials.dynamoDBMapper().load(DB_BankDetails.class, restaurantId);
    }

}
