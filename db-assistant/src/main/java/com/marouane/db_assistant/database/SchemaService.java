package com.marouane.db_assistant.database;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class SchemaService {

    public String extractSchemaInfo(DataSource dataSource) {
        StringBuilder schema = new StringBuilder("Database schema:\n");
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet tables = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (tables.next()){
                    String tableName = tables.getString("TABLE_NAME");
                    String tableSchema = tables.getString("TABLE_SCHEM");

                    schema.append("Table: ")
                            .append(tableSchema != null ? tableSchema + "." : "")
                            .append(tableName)
                            .append(" (\n");


                    try(ResultSet columns = metaData.getColumns(connection.getCatalog(), tableSchema, tableName, null)){
                        boolean first = true;
                        while (columns.next()){
                            if (!first) {
                                schema.append(",\n");
                            }
                            schema.append(" ")
                                    .append(columns.getString("COLUMN_NAME"))
                                    .append(" ")
                                    .append(columns.getString("TYPE_NAME"));
                            first = false;
                        }
                    }
                    schema.append("\n)\n\n");
                }

            }
        }catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        return schema.toString();
    }
}
