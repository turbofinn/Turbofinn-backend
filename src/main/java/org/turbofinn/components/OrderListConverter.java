package org.turbofinn.components;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.turbofinn.dbmappers.DB_Order;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderListConverter implements DynamoDBTypeConverter<String, List<DB_Order.OrderList>> {
    private static final Gson gson = new Gson();
    private static final Type type = new TypeToken<ArrayList<DB_Order.OrderList>>(){}.getType();

    @Override
    public String convert(List<DB_Order.OrderList> orderLists) {
        return gson.toJson(orderLists);
    }

    @Override
    public List<DB_Order.OrderList> unconvert(String orderListsJson) {
        return gson.fromJson(orderListsJson, type);
    }
}

