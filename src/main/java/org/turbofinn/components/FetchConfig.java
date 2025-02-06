package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_Config;
import org.turbofinn.util.Constants;
import org.yaml.snakeyaml.scanner.Constant;

public class FetchConfig implements RequestHandler<FetchConfig.FetchConfigInput, FetchConfig.FetchConfigOutput> {
    public static void main(String[] args) {
        FetchConfigInput input = new FetchConfigInput();
        input.setKey("CATEGORY_LIST");
        System.out.println(new Gson().toJson(new FetchConfig().handleRequest(input,null)));
    }

    @Override
    public FetchConfig.FetchConfigOutput handleRequest(FetchConfig.FetchConfigInput input, Context context) {

        if(input==null || input.getKey()==null){
            return new FetchConfigOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }
        DB_Config dbConfig = DB_Config.fetchByKey(input.key);
        if(dbConfig==null){
            return new FetchConfigOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,"Please input a valid Key"),null);
        }
        else{
            return new FetchConfigOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),dbConfig);
        }



    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class FetchConfigOutput {
        private Response response;
        private DB_Config dbConfig;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response{
        private int responseCode;
        private String responseMessage;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class FetchConfigInput {
        private String key;
    }
}
