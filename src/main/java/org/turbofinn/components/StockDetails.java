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
import org.turbofinn.dbmappers.DB_Stock;
import org.turbofinn.util.Constants;

import java.util.List;

public class StockDetails implements RequestHandler<StockDetails.StockInput, StockDetails.StockOutput> {

    public static void main(String[] args) {
        StockDetails.StockInput input = new StockDetails.StockInput();
        input.setStockId("9ca0-4307c9d28e2f");
        input.setRestaurantId("308bc44a-de00-488e-b980-5ee0797e82e2");
        input.setName("milk");
        input.setQuantity("2");
        input.setUnit("4");
        input.setUnitPrice("50");
        input.setTotalPrice("2500");
        input.setPaymentStatus("PAID");
        input.setPaymentMode("cash");
        input.setPaymentDate("08/10/2024");
        input.setAction("CREATE");
        System.out.println(new Gson().toJson(new StockDetails().handleRequest(input,null)));
    }

    @Override
    public StockDetails.StockOutput handleRequest(StockDetails.StockInput input, Context context) {
        if(input == null ){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        else if(input.getRestaurantId()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide RestaurantId"), null);
        }
        else if(input.getStockId()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide StockId"), null);
        }
        else if(input.getName()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide stock name"), null);
        }
        else if(input.getQuantity()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide stock quantity"), null);
        }
        else if(input.getUnit()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide stock unit"), null);
        }

        switch (DB_Feedback.ActionType.getActionType(input.action)) {
            case CREATE:
                return createNewStock(input);
            case UPDATE:
                return updateStock(input);
            case DELETE:
                return deleteStock(input);
            case FETCH:
                return fetchAllStock(input);
            default:
                return new StockDetails.StockOutput(new StockDetails.Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
    }

    private StockDetails.StockOutput createNewStock(StockDetails.StockInput input) {
        if(input.getRestaurantId()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Restaurant id"), null);
        }
        if(input.getStockId()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Stock id"), null);
        }
        if(input.getName()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Stock name"), null);
        }
        if(input.getQuantity()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Stock Quantity"), null);
        }
        if(input.getUnit()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide Stock Unit"), null);
        }

        DB_Stock dbStock = new DB_Stock();
        dbStock.setRestaurantId(input.getRestaurantId());
        dbStock.setStockId(input.getStockId());
        dbStock.setName(input.getName());
        dbStock.setQuantity(input.getQuantity());
        dbStock.setUnit(input.getUnit());
        dbStock.setUnitPrice(input.getUnitPrice());
        dbStock.setTotalPrice(input.getTotalPrice());
        dbStock.setPaymentStatus(input.getPaymentStatus());
        dbStock.setPaymentDate(input.getPaymentDate());
        dbStock.setPaymentMode(input.getPaymentMode());
        dbStock.save();
        return new StockDetails.StockOutput(new StockDetails.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), null);
    }

    private StockDetails.StockOutput updateStock(StockDetails.StockInput input) {
        if(input.getStockId()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Stock id is null"), null);
        }
        DB_Stock dbStock = DB_Stock.fetchStockByID(input.stockId);
        dbStock.setRestaurantId(input.getRestaurantId());
        dbStock.setStockId(input.getStockId());
        dbStock.setName(input.getName());
        dbStock.setQuantity(input.getQuantity());
        dbStock.setUnit(input.getUnit());
        dbStock.setUnitPrice(input.getUnitPrice());
        dbStock.setTotalPrice(input.getTotalPrice());
        dbStock.setPaymentStatus(input.getPaymentStatus());
        dbStock.setPaymentDate(input.getPaymentDate());
        dbStock.setPaymentMode(input.getPaymentMode());
        dbStock.save();
        return new StockDetails.StockOutput(new StockDetails.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), null);
    }

    private StockDetails.StockOutput deleteStock(StockDetails.StockInput input) {
        if(input.getStockId()==null){
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Please provide stock id"), null);
        }
        DB_Stock dbStock = DB_Stock.fetchStockByID(input.stockId);
        if (dbStock == null) {
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Stock not found"), null);
        }
        AWSCredentials.dynamoDBMapper().delete(dbStock);
        return new StockDetails.StockOutput(new StockDetails.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), null);
    }

    private StockDetails.StockOutput fetchAllStock(StockDetails.StockInput input ) {
        if (input.getStockId() == null) {
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.INVALID_INPUTS_RESPONSE_CODE, Constants.INVALID_INPUTS_RESPONSE_MESSAGE), null);
        }
        List<DB_Stock> stock = DB_Stock.fetchStocksByRestaurantID(input.getRestaurantId());

        if (stock == null) {
            return new StockDetails.StockOutput(new StockDetails.Response(Constants.GENERIC_RESPONSE_CODE,"Stock not found"), null);
        }
        return new StockDetails.StockOutput(new StockDetails.Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), stock);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockInput {
        String stockId;
        String restaurantId;
        String name;
        String quantity;
        String unit;
        String unitPrice;
        String totalPrice;
        String paymentStatus;
        String paymentMode;
        String paymentDate;
        String action;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public  static class Response{
        int responseCode;
        String message;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class StockOutput {
        public StockDetails.Response response;
        List<DB_Stock> stocks;
    }
}
