package org.turbofinn.DynamoToRDS;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import org.turbofinn.util.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class DynamoDBToRDSHandler implements RequestHandler<DynamodbEvent, String> {
    @Override
    public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
            if (record == null || record.getDynamodb() == null) continue;

            String dynamoDbTableName = record.getEventSourceARN().split(":table/")[1].split("/")[0];
            if (TableMappings.isTableMapped(dynamoDbTableName)) {
                TableMapping mapping = TableMappings.getTableMapping(dynamoDbTableName);
                switch (record.getEventName()) {
                    case "INSERT":
                        handleInsert(mapping, record.getDynamodb().getNewImage());
                        break;
                    case "MODIFY":
                        handleModify(mapping, record.getDynamodb().getNewImage());
                        break;
                    case "REMOVE":
                        handleRemove(mapping, record.getDynamodb().getOldImage());
                        break;
                }
            }
        }
        return "Processed " + dynamodbEvent.getRecords().size() + " records.";
    }

    private void handleInsert(TableMapping mapping, Map<String, AttributeValue> newImage) {
        String sql = generateInsertSQL(mapping);
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            System.out.println("connection "+connection);
            setPreparedStatementValues(ps, mapping.columns, mapping.excludedColumns, newImage);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleModify(TableMapping mapping, Map<String, AttributeValue> newImage) {
        String sql = generateUpdateSQL(mapping);

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            setPreparedStatementValues(ps, mapping.columns, mapping.excludedColumns, newImage);
            ps.setString(mapping.columns.length - mapping.excludedColumns.length + 1, newImage.get(mapping.primaryKey).getS());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleRemove(TableMapping mapping, Map<String, AttributeValue> oldImage) {
        if (oldImage == null) {
            System.out.println("OldImage is null for REMOVE event.");
            return;
        }

        String sql = "DELETE FROM " + mapping.rdsTableName + " WHERE " + mapping.primaryKey + " = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, oldImage.get(mapping.primaryKey).getS());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.RDS_URL, Constants.RDS_USERNAME, Constants.RDS_PASSWORD);
    }

    private String generateInsertSQL(TableMapping mapping) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(mapping.rdsTableName);
        sql.append(" (");

        for (String column : mapping.columns) {
            if (!isExcluded(column, mapping.excludedColumns)) {
                sql.append(column).append(", ");
            }
        }
        sql.setLength(sql.length() - 2); // Remove the last comma
        sql.append(") VALUES (");

        for (String column : mapping.columns) {
            if (!isExcluded(column, mapping.excludedColumns)) {
                sql.append("?, ");
            }
        }
        sql.setLength(sql.length() - 2); // Remove the last comma
        sql.append(")");

        return sql.toString();
    }

    private String generateUpdateSQL(TableMapping mapping) {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(mapping.rdsTableName);
        sql.append(" SET ");

        for (String column : mapping.columns) {
            if (!isExcluded(column, mapping.excludedColumns)) {
                sql.append(column).append(" = ?, ");
            }
        }
        sql.setLength(sql.length() - 2); // Remove the last comma
        sql.append(" WHERE ").append(mapping.primaryKey).append(" = ?");

        return sql.toString();
    }

    private void setPreparedStatementValues(PreparedStatement ps, String[] columns, String[] excludedColumns, Map<String, AttributeValue> image) throws SQLException {
        int index = 1;
        for (String column : columns) {
            if (!isExcluded(column, excludedColumns)) {
                AttributeValue value = image.get(column);
                if (value == null) {
                    ps.setNull(index++, java.sql.Types.NULL);
                } else if (value.getS() != null) {
                    ps.setString(index++, value.getS());
                } else if (value.getN() != null) {
                    ps.setBigDecimal(index++, new java.math.BigDecimal(value.getN()));
                } else if (value.getBOOL() != null) {
                    ps.setBoolean(index++, value.getBOOL());
                } else {
                    // Handle other possible attribute types if needed
                    ps.setObject(index++, value.toString());
                }
            }
        }
    }


    private boolean isExcluded(String column, String[] excludedColumns) {
        for (String excludedColumn : excludedColumns) {
            if (excludedColumn.equals(column)) {
                return true;
            }
        }
        return false;
    }
}
