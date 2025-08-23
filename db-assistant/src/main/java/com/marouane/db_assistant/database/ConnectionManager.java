package com.marouane.db_assistant.database;

import com.marouane.db_assistant.exception.DatabaseConnectionException;
import com.marouane.db_assistant.user.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ConnectionManager {
    private final Map<Integer, ActiveConnection> activeConnections = new ConcurrentHashMap<>();
    private final ConnexionRepository connectionRepo;

    //Activate a connexion for a user
    public void activateConnection(Authentication connectedUser, Integer connectionId) {
        User user = (User) connectedUser.getPrincipal();

        DatabaseConnection conn = connectionRepo
                .findByIdAndUser(connectionId, user)
                .orElseThrow(() -> new SecurityException("Connection not found"));
        DataSource ds = createDatasource(conn);
        activeConnections.put(user.getId(), new ActiveConnection(conn, ds));
    }

    //creates a pooled Datasource
    private DataSource createDatasource(DatabaseConnection conn) {
        // create hikari config

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(conn.getJdbcUrl());
        config.setUsername(conn.getUsername());
        config.setPassword(conn.getPassword());

        //test connection
        try(HikariDataSource testDs = new HikariDataSource(config);
            Connection testConn = testDs.getConnection()) {
            if (!testConn.isValid(2)) {
                throw new DatabaseConnectionException("Connection invalid");
            }

            return new HikariDataSource(config);

        }catch (SQLException e) {
            throw new DatabaseConnectionException("Connection test failed: " + e.getMessage());
        }
    }


    public DataSource getActiveDataSource(Integer userId) {
        return Optional.ofNullable(activeConnections.get(userId))
                .map(ActiveConnection::dataSource)
                .orElseThrow(() -> new IllegalStateException("No active datasource"));
    }

    public DatabaseConnection getActiveConnection(Integer userId) {
        return Optional.ofNullable(activeConnections.get(userId))
                .map(ActiveConnection::connection)
                .orElseThrow(() -> new IllegalStateException("No active connection for user " + userId));
    }

    public void cleanupUserConnection(Integer userId) {
        ActiveConnection  activeConn  = activeConnections.remove(userId);
        if (activeConn != null && activeConn.dataSource() instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }


    public List<DatabaseConnection> getAllConnections(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return connectionRepo.findAllByUser(user);

    }

    public boolean testConnection(DbConnectionRequest request) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:%s://%s:%d/%s",
                request.getDbType(), request.getHost(), request.getPort(), request.getDatabase()));
        config.setUsername(request.getUsername());
        config.setPassword(request.getPassword());

        try (HikariDataSource ds = new HikariDataSource(config);
             Connection conn = ds.getConnection()) {

            return conn.isValid(2); // returns true if connection is valid

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Test connection failed: " + e.getMessage());
        }
    }

    public void deleteConnection(Authentication connectedUser, Integer connectionId) {
        User user = (User) connectedUser.getPrincipal();

        // Fetch the connection for this user
        DatabaseConnection conn = connectionRepo.findByIdAndUser(connectionId, user)
                .orElseThrow(() -> new SecurityException("Connection not found or does not belong to the user"));

        // Remove from active connections if currently active
        ActiveConnection activeConn = activeConnections.get(user.getId());
        if (activeConn != null && activeConn.connection().getId().equals(connectionId)) {
            // Close the HikariDataSource if active
            if (activeConn.dataSource() instanceof HikariDataSource hikari) {
                hikari.close();
            }
            activeConnections.remove(user.getId());
        }

        // Delete from repository
        connectionRepo.delete(conn);
    }



}
