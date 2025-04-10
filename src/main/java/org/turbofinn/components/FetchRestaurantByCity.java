package org.turbofinn.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.*;
import org.opensearch.action.admin.cluster.decommission.awareness.get.GetDecommissionStateRequest;
import org.turbofinn.dbmappers.DB_Restaurant;
import org.turbofinn.util.Constants;

import java.util.List;

public class FetchRestaurantByCity implements RequestHandler<FetchRestaurantByCity.FetchRestaurantByCityInput,FetchRestaurantByCity.FetchRestaurantByCityOutput > {


    @Override
    public FetchRestaurantByCityOutput handleRequest(FetchRestaurantByCityInput input, Context context) {

        System.out.println(new Gson().toJson(input));
        if(input==null){
            return new FetchRestaurantByCityOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_MESSAGE,Constants.INVALID_INPUTS_RESPONSE_CODE),null);
        }
        if(input.getCity()==null || input.getLatitude()==null || input.getLongitude()==null){
            return new FetchRestaurantByCityOutput(new Response("city, longitude or latitude can not be null",Constants.INVALID_INPUTS_RESPONSE_CODE),null);
        }

        List<DB_Restaurant> restaurantList = DB_Restaurant.fetchByCity(input.getCity());

        double inputLat = Double.parseDouble(input.getLatitude());
        double inputLon = Double.parseDouble(input.getLongitude());

        if(restaurantList!=null){
        restaurantList.sort((r1, r2) -> {
//            double d1 = calculateDistance(inputLat, inputLon, r1.get, r1.getLongitude());
//            double d2 = calculateDistance(inputLat, inputLon, r2.getLatitude(), r2.getLongitude());
            double d1 =0 ;
            double d2 =0;
            return Double.compare(d1, d2);
        });
        }

        // Pick top 5
        List<DB_Restaurant> top5 = restaurantList.stream().limit(5).toList();

        return new FetchRestaurantByCityOutput(
                new Response("Top 5 nearby restaurants fetched successfully", 200),
                top5
        );





    }



    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }



    @Getter@ToString@Setter@NoArgsConstructor@AllArgsConstructor
    public class FetchRestaurantByCityOutput {
        Response response;
        List<DB_Restaurant> restaurantList;


    }

    @Getter@ToString@Setter@NoArgsConstructor@AllArgsConstructor
    public class Response{
        String message;
        int code;
    }

    @Getter@ToString@Setter@NoArgsConstructor@AllArgsConstructor
    public class FetchRestaurantByCityInput {
        String longitude;
        String latitude;
        String city;
    }
}
