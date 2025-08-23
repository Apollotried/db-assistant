package com.marouane.db_assistant.database;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbConnectionMapper {

    public DbConnectionResponse toDto(DatabaseConnection connection) {
        DbConnectionResponse response = new DbConnectionResponse();
        response.setId(connection.getId());
        response.setName(connection.getName());
        response.setHost(connection.getHost());
        response.setPort(connection.getPort());
        response.setDbType(connection.getDbType());
        response.setDatabase(connection.getDatabase());
        response.setUsername(connection.getUsername());
        return response;
    }

    public List<DbConnectionResponse> toDtoList(List<DatabaseConnection> connections) {
        return connections.stream()
                .map(this::toDto)
                .toList();
    }
}
