package org.turbofinn.DynamoToRDS;

import java.util.HashMap;
import java.util.Map;

public class TableMappings {
    private static final Map<String, TableMapping> mappings = new HashMap<>();

    static {
        mappings.put("Feedback", new TableMapping(
                "Feedback",
                "feedbackId",
                new String[]{"feedbackId", "createdDate", "message", "rating", "restaurantId", "timestamp", "updatedDate", "userId"},
                new String[]{}
        ));
        mappings.put("Items", new TableMapping(
                "Items",
                "itemId",
                new String[]{
                        "itemId",
                        "category",
                        "createdDate",
                        "cuisine",
                        "description",
                        "eta",
                        "flag",
                        "isDeleted",
                        "itemPicture",
                        "name",
                        "price",
                        "restaurantId",
                        "tag",
                        "type",
                        "updatedDate"
                },
                new String[]{}
        ));
        mappings.put("Offers", new TableMapping(
                "Offers",
                "offerId",
                new String[]{
                        "offerId",
                        "createdDate",
                        "details",
                        "endDate",
                        "image",
                        "offerName",
                        "referenceId",
                        "restaurantId",
                        "startDate",
                        "updatedDate"
                },
                new String[]{}
        ));
        mappings.put("Order", new TableMapping(
                "Orders",
                "orderId",
                new String[]{
                        "orderId",
                        "createdDate",
                        "customerFeedback",
                        "customerRating",
                        "customerRequest",
                        "orderLists",
                        "orderStatus",
                        "paymentStatus",
                        "restaurantId",
                        "tableNo",
                        "totalAmount",
                        "updatedDate",
                        "userId",
                        "orderDate"
                },
                new String[]{}
        ));
        mappings.put("Payments", new TableMapping(
                "Payments",
                "paymentId",
                new String[]{
                        "paymentId",
                        "createdDate",
                        "orderId",
                        "paymentAmount",
                        "paymentDate",
                        "paymentStatus",
                        "restaurantId",
                        "tableNo",
                        "updatedDate",
                        "userId",
                        "paymentMode"
                },
                new String[]{}
        ));
        mappings.put("Restaurant", new TableMapping(
                "Restaurant",
                "restaurantId",
                new String[]{
                        "restaurantId",
                        "address",
                        "city",
                        "contactNo",
                        "createdDate",
                        "emailId",
                        "name",
                        "pincode",
                        "updatedDate"
                },
                new String[]{}
        ));
        mappings.put("Table", new TableMapping(
                "Tables",
                "tableId",
                new String[]{
                        "tableId",
                        "mobileNo",
                        "paymentStatus",
                        "restaurantId",
                        "status",
                        "tableNo",
                        "userId"
                },
                new String[]{}
        ));
        mappings.put("User", new TableMapping(
                "User",
                "userId",
                new String[]{
                        "userId",
                        "userName",
                        "createdDate",
                        "mobileNo",
                        "updatedDate"
                },
                new String[]{}
        ));
    }

    public static TableMapping getTableMapping(String dynamoDbTableName) {
        return mappings.get(dynamoDbTableName);
    }

    public static boolean isTableMapped(String dynamoDbTableName) {
        return mappings.containsKey(dynamoDbTableName);
    }
}

class TableMapping {
    String rdsTableName;
    String primaryKey;
    String[] columns;
    String[] excludedColumns;

    TableMapping(String rdsTableName, String primaryKey, String[] columns, String[] excludedColumns) {
        this.rdsTableName = rdsTableName;
        this.primaryKey = primaryKey;
        this.columns = columns;
        this.excludedColumns = excludedColumns;
    }

    public String getRdsTableName() {
        return rdsTableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String[] getColumns() {
        return columns;
    }

    public String[] getExcludedColumns() {
        return excludedColumns;
    }
}
