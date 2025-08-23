package com.marouane.db_assistant.database;

import javax.sql.DataSource;

public record ActiveConnection(
        DatabaseConnection connection,
        DataSource dataSource
) {
}
