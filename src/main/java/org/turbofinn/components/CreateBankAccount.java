package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.dbmappers.DB_BankDetails;
import org.turbofinn.util.Constants;

public class CreateBankAccount implements RequestHandler<CreateBankAccount.CreateBankAccountInput,CreateBankAccount.CreateBankAccountOutput> {
    @Override
    public CreateBankAccount.CreateBankAccountOutput handleRequest(CreateBankAccount.CreateBankAccountInput input, Context context) {
        if (!isValidInput(input)) {
            return new CreateBankAccountOutput(
                    new Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE)
            );
        }

        try {
            DB_BankDetails bankDetails = new DB_BankDetails();
            bankDetails.setRestaurantId(input.getRestaurantId());
            bankDetails.setAccountNumber(input.getAccountNumber());
            bankDetails.setIfsc(input.getIfsc());
            bankDetails.setAccHolderName(input.getAccHolderName());
            bankDetails.setRazorpayAccId(input.getRazorpayAccId());
            bankDetails.setEmail(input.getEmail());
            bankDetails.setMobileNumber(input.getMobileNumber());

            bankDetails.save();

            return new CreateBankAccountOutput(
                    new Response(Constants.SUCCESS_RESPONSE_CODE, "Bank account created successfully")
            );

        } catch (Exception e) {
            context.getLogger().log("Error creating bank account: " + e.getMessage());
            return new CreateBankAccountOutput(
                    new Response(Constants.ERROR_RESPONSE_CODE, "Failed to create bank account")
            );
        }


}


    private boolean isValidInput(CreateBankAccountInput input) {
        return input != null &&
                input.getRestaurantId() != null &&
                input.getAccountNumber() != null &&
                input.getIfsc() != null &&
                input.getAccHolderName() != null &&
                input.getEmail() != null &&
                input.getMobileNumber() != null;
    }


    @Getter@Setter@AllArgsConstructor@NoArgsConstructor
    public class CreateBankAccountOutput{
        Response response;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public class CreateBankAccountInput{
        String restaurantId;
        String accountNumber;
        String ifsc;
        String accHolderName;
        String razorpayAccId;
        String email;
        String mobileNumber;
    }


}
