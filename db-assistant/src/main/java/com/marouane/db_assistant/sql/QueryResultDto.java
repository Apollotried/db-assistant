package com.marouane.db_assistant.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryResultDto {
    private boolean success;
    private String message;
    private List<Map<String, Object>> data;
    private List<String> columns;
    private int rowCount;
    private String queryType;
    private int affectedRows;
    private boolean isAggregateQuery; // New field
    private Map<String, Object> aggregateResult; // New field for single results


    // For SELECT queries with result sets
    public QueryResultDto(boolean success, List<Map<String, Object>> data,
                          List<String> columns, int rowCount) {
        this.success = success;
        this.data = data;
        this.columns = columns;
        this.rowCount = rowCount;
        this.queryType = "SELECT";
        this.isAggregateQuery = false;
    }

    // For aggregate queries (COUNT, SUM, AVG, etc.)
    public QueryResultDto(boolean success, Map<String, Object> aggregateResult,
                          String functionName, Object resultValue) {
        this.success = success;
        this.aggregateResult = aggregateResult;
        this.queryType = "SELECT";
        this.isAggregateQuery = true;
        this.rowCount = 1; // Always 1 row for aggregates
        this.message = functionName + " result: " + resultValue;
    }

    // For DML operations
    public QueryResultDto(boolean success, int affectedRows, String queryType) {
        this.success = success;
        this.affectedRows = affectedRows;
        this.queryType = queryType;
        this.message = queryType + " operation completed successfully. Affected rows: " + affectedRows;
    }

    // For errors
    public QueryResultDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }



}
