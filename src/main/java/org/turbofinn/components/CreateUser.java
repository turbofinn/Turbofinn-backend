package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_User;
import org.turbofinn.util.Constants;

public class CreateUser implements RequestHandler<CreateUser.CreateUserInput,CreateUser.CreateUserOutput> {

    public static void main(String[] args) {
        CreateUserInput createUserInput = new CreateUserInput();
        createUserInput.setMobileNo("7269097224");
        System.out.println(new Gson().toJson(new CreateUser().handleRequest(createUserInput,null)));
    }


    @Override
    public CreateUser.CreateUserOutput handleRequest(CreateUser.CreateUserInput createUserInput, Context context) {
        if(createUserInput == null || createUserInput.mobileNo==null){
            return new CreateUserOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        DB_User dbUser = new DB_User();
        dbUser.setMobileNo(createUserInput.getMobileNo());
        dbUser.save();
        return new CreateUserOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));

    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateUserOutput {
        public Response response;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateUserInput {
        String mobileNo;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
