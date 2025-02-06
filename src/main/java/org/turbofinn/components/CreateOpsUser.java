package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.turbofinn.dbmappers.DB_OpsUser;
import org.turbofinn.util.Constants;

public class CreateOpsUser implements RequestHandler<CreateOpsUser.CreateUserInput,CreateOpsUser.CreateUserOutput> {
    public static void main(String[] args) {
        System.out.println(new CreateOpsUser().handleRequest(new Gson().fromJson("{\n" +
                "  \"designation\": \"Manager\",\n" +
                "  \"restaurantID\": \"12345\",\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"gender\": \"Male\",\n" +
                "  \"dob\": \"1990-01-01\",\n" +
                "  \"mobileNo\": \"9876543210\",\n" +
                "  \"profilePicture\": \"profile.jpg\",\n" +
                "  \"email\": \"johndoe@example.com\"\n" +
                "}\n", CreateUserInput.class),null));
    }

    @Override
    public CreateOpsUser.CreateUserOutput handleRequest(CreateOpsUser.CreateUserInput input, Context context) {

        if(input==null){
            return new CreateOpsUser.CreateUserOutput(new CreateOpsUser.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }
        if(StringUtils.isAnyBlank(input.mobileNo,input.restaurantID,input.designation,input.name,input.profilePicture)){
            return new CreateOpsUser.CreateUserOutput(new CreateOpsUser.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE));
        }



        DB_OpsUser existingUSer = DB_OpsUser.fetchOpsUserByMobileNo(input.getMobileNo());
        if(existingUSer!=null){
            return new CreateOpsUser.CreateUserOutput(new CreateOpsUser.Response(Constants.GENERIC_RESPONSE_CODE,"User already exist with this mobile Number"));
        }


        DB_OpsUser opsUser = new DB_OpsUser();
        opsUser.setDesignation(input.getDesignation());
        opsUser.setRestaurantID(input.restaurantID);
        opsUser.setName(input.getName());
        opsUser.setGender(input.getGender());
        opsUser.setDob(input.getDob());
        opsUser.setMobileNo(input.getMobileNo());
        opsUser.setProfilePicture(input.profilePicture);
        opsUser.setEmail(input.getEmail());
        opsUser.save();
        return new CreateOpsUser.CreateUserOutput(new CreateOpsUser.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE));

    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor

    public static class CreateUserOutput {
        public Response response;
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class CreateUserInput {
        public String designation;
        public String restaurantID;
        public String name;
        public String gender;
        public String dob;
        public String mobileNo;
        public String profilePicture;
        public String email;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
