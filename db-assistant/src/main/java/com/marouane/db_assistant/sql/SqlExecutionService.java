package com.marouane.db_assistant.sql;

import com.marouane.db_assistant.database.ConnectionManager;
import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SqlExecutionService {
    private final ConnectionManager connectionManager;
    private final QueryHistoryService queryHistoryService;

    public QueryResultDto execute(Authentication connectedUser, String sql) throws SQLException {
        User user = (User) connectedUser.getPrincipal();

        DataSource dataSource = connectionManager.getActiveDataSource(user.getId());
        DatabaseConnection activeConnection = connectionManager.getActiveConnection(user.getId());
        // validate syntax without executing

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Syntax valid if no exception
        }catch (SQLException e) {
            throw new SQLException("SQL syntax error: " + e.getMessage(), e);
        }

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            boolean hasResultSet = stmt.execute(sql);
            queryHistoryService.logQuery(user, activeConnection, sql);

            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    List<String> columns = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columns.add(metaData.getColumnName(i));
                    }

                    List<Map<String, Object>> data = new ArrayList<>();
                    int rowCount = 0;

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(columns.get(i-1), rs.getObject(i));
                        }
                        data.add(row);
                        rowCount++;
                    }

                    // Check if this is an aggregate query
                    if (isAggregateQuery(sql) && rowCount == 1 && columnCount == 1) {
                        Object resultValue = data.get(0).values().iterator().next();
                        String functionName = detectAggregateFunction(sql);
                        Map<String, Object> aggregateResult = Map.of(
                                "function", functionName,
                                "value", resultValue,
                                "displayName", getDisplayName(functionName)
                        );
                        return new QueryResultDto(true, aggregateResult, functionName, resultValue);
                    }

                    return new QueryResultDto(true, data, columns, rowCount);
                }
            } else {
                int affectedRows = stmt.getUpdateCount();
                String queryType = getQueryType(sql);
                return new QueryResultDto(true, affectedRows, queryType);
            }
        } catch (SQLException e) {
            return new QueryResultDto(false, "Execution error: " + e.getMessage());
        }
    }

    private boolean isAggregateQuery(String sql) {
        String upperSql = sql.toUpperCase();
        return upperSql.contains("COUNT(") || upperSql.contains("SUM(") ||
                upperSql.contains("AVG(") || upperSql.contains("MAX(") ||
                upperSql.contains("MIN(") || upperSql.contains("GROUP BY");
    }

    private String detectAggregateFunction(String sql) {
        String upperSql = sql.toUpperCase();
        if (upperSql.contains("COUNT(")) return "COUNT";
        if (upperSql.contains("SUM(")) return "SUM";
        if (upperSql.contains("AVG(")) return "AVG";
        if (upperSql.contains("MAX(")) return "MAX";
        if (upperSql.contains("MIN(")) return "MIN";
        return "AGGREGATE";
    }

    private String getDisplayName(String function) {
        switch (function) {
            case "COUNT": return "Total Count";
            case "SUM": return "Sum";
            case "AVG": return "Average";
            case "MAX": return "Maximum";
            case "MIN": return "Minimum";
            default: return "Result";
        }
    }

    private String getQueryType(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "UNKNOWN";
        }

        String upperSql = sql.trim().toUpperCase();

        // Check for SELECT first (most common)
        if (upperSql.startsWith("SELECT")) {
            return "SELECT";
        }

        // Check for INSERT variations
        if (upperSql.startsWith("INSERT")) {
            return "INSERT";
        }

        // Check for UPDATE
        if (upperSql.startsWith("UPDATE")) {
            return "UPDATE";
        }

        // Check for DELETE
        if (upperSql.startsWith("DELETE")) {
            return "DELETE";
        }

        // Check for DDL commands
        if (upperSql.startsWith("CREATE")) {
            return "CREATE";
        }
        if (upperSql.startsWith("ALTER")) {
            return "ALTER";
        }
        if (upperSql.startsWith("DROP")) {
            return "DROP";
        }
        if (upperSql.startsWith("TRUNCATE")) {
            return "TRUNCATE";
        }

        // Check for other common operations
        if (upperSql.startsWith("MERGE")) {
            return "MERGE";
        }
        if (upperSql.startsWith("CALL")) {
            return "CALL";
        }
        if (upperSql.startsWith("EXPLAIN")) {
            return "EXPLAIN";
        }

        return "UNKNOWN";
    }


}

