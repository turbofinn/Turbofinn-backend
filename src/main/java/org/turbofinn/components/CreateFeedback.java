package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.aws.AWSCredentials;
import org.turbofinn.dbmappers.DB_Feedback;
import org.turbofinn.util.Constants;

import java.util.Date;
import java.util.List;

public class CreateFeedback implements RequestHandler<CreateFeedback.CreateFeedbackInput, CreateFeedback.CreateFeedbackOutput> {

    public static void main(String[] args) {
        CreateFeedback.CreateFeedbackInput input = new CreateFeedback.CreateFeedbackInput();
        input.setFeedbackId("d1d07aae-39a9-479e-9ca0-4307c9d28e2f");
        input.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
        input.setUserId("bef2578c-8346-4b03-8971-20da77c4bedd");
        input.setMessage("delicious food and service");
        input.setRating("4");
        input.setAction("FETCH");

        System.out.println(new Gson().toJson(new CreateFeedback().handleRequest(input,null)));
    }

    @Override
    public CreateFeedback.CreateFeedbackOutput handleRequest(CreateFeedback.CreateFeedbackInput input, Context context) {
        if(input == null ){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        else if(input.getRestaurantId()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide RestaurantId"), null);
        }
        else if(input.getUserId()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide UserId"), null);
        }
        else if(input.getMessage()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide your feedback message"), null);
        }
        else if(input.getRating()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide rating"), null);
        }

        switch (DB_Feedback.ActionType.getActionType(input.action)) {
            case CREATE:
                return createNewFeedback(input);
            case UPDATE:
                return updateFeedback(input);
            case DELETE:
                return deleteFeedback(input);
            case FETCH:
                return fetchFeedback(input);
            default:
                return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
    }

    private CreateFeedback.CreateFeedbackOutput deleteFeedback(CreateFeedback.CreateFeedbackInput input) {
        if(input.getFeedbackId()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Feedback id is null"), null);
        }
        DB_Feedback dbFeedback = DB_Feedback.fetchFeedbackByID(input.feedbackId);
        if (dbFeedback == null) {
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE, "Feedback not found"), null);
        }
        AWSCredentials.dynamoDBMapper().delete(dbFeedback);
        return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), null);
    }

    private CreateFeedback.CreateFeedbackOutput updateFeedback(CreateFeedback.CreateFeedbackInput input) {
        if(input.getFeedbackId()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Feedback id is null"), null);
        }
        DB_Feedback dbFeedback = DB_Feedback.fetchFeedbackByID(input.feedbackId);
        dbFeedback.setRestaurantId(input.getRestaurantId());
        dbFeedback.setUserId(input.getUserId());
        dbFeedback.setMessage(input.getMessage());
        dbFeedback.setRating(input.getRating());
        dbFeedback.save();
        return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), null);

    }

    private CreateFeedback.CreateFeedbackOutput createNewFeedback(CreateFeedback.CreateFeedbackInput input) {
        if(input.getRestaurantId()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Restaurant id"), null);
        }
        if(input.getUserId()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide User id"), null);
        }
        if(input.getMessage()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide your feedback message"), null);
        }
        if(input.getRating()==null){
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide rating"), null);
        }
        
        DB_Feedback dbFeedback = new DB_Feedback();
        dbFeedback.setRestaurantId(input.getRestaurantId());
        dbFeedback.setUserId(input.getUserId());
        dbFeedback.setMessage(input.getMessage());
        dbFeedback.setRating(input.getRating());
        dbFeedback.save();
        return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), null);
    }

    private CreateFeedback.CreateFeedbackOutput fetchFeedback(CreateFeedback.CreateFeedbackInput input) {
        if (input.getFeedbackId() == null) {
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        List<DB_Feedback> feedback = DB_Feedback.fetchFeedbackByRestaurantID(input.getRestaurantId());

        if (feedback == null) {
            return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.INVALID_INPUTS_RESPONSE_CODE, "No feedback provided"), null);
        }
        return new CreateFeedback.CreateFeedbackOutput(new CreateFeedback.Response(Constants.SUCCESS_RESPONSE_CODE, Constants.SUCCESS_RESPONSE_MESSAGE), feedback);
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateFeedbackInput {
        String feedbackId;
        String userId;
        String restaurantId;
        String message;
        String rating;
        Date timestamp;
        String action;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class CreateFeedbackOutput {
        public CreateFeedback.Response response;
        List<DB_Feedback> feedback;
    }

}
