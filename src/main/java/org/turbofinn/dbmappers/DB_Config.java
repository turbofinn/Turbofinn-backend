package org.turbofinn.dbmappers;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.google.gson.Gson;
import lombok.*;
import org.turbofinn.aws.AWSCredentials;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBTable(tableName = "Config")
public class DB_Config extends DB_DateTable {
    public static void main(String[] args) {

        DB_Config config = new DB_Config();
        config.setKey("NBFC_LIST");
        config.setIsActive("true");
        config.setClientConfig(1);
        config.setServerConfig(1);
        config.setValue("DMI,VCPL,Finnable,TVS,Northern-Arc,Utkarsh,TVS-Online,HDB,SBM,Piramal,HDB-Direct,AXIS,Tata-Capital");
        config.setImages(Map.of(
                "DMI", "https://s3.amazonaws.com/bucket-name/DMI.png",
                "VCPL", "https://s3.amazonaws.com/bucket-name/VCPL.png",
                "Finnable", "https://s3.amazonaws.com/bucket-name/Finnable.png"
        ));
        config.save();
    }

    @DynamoDBHashKey
    private String key;
    private String isActive;
    private int clientConfig;
    private int serverConfig;
    private String value;
    private Map<String, String> images;



    public void save(){
        AWSCredentials.dynamoDBMapper().save(this);
        System.out.println("*** Config is saved  *** " + new Gson().toJson(this));
    }

    public DB_Config fetchByKey(String key){
        return (key == null) ? null : AWSCredentials.dynamoDBMapper().load(DB_Config.class,key);
    }
}
