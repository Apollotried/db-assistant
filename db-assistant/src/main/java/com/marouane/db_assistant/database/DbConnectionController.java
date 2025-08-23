package com.marouane.db_assistant.database;

import com.marouane.db_assistant.exception.DatabaseConnectionException;
import com.marouane.db_assistant.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("db")
@RequiredArgsConstructor

@Tag(name = "Database Connections", description = "Manage and activate user database connections")
public class DbConnectionController {
    private final ConnectionManager connectionManager;
    private final ConnexionRepository connectionRepo;
    private final DbConnectionMapper mapper;
    private final SchemaService schemaService;

    @PostMapping(produces = "application/json")
    @Operation(
            summary = "Create and activate a database connection",
            description = "Stores a new database connection configuration for the authenticated user and immediately activates it.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Database connection details for creating and activating a connection",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = """
                        {
                          "name": "MyPostgresDB",
                          "dbType": "postgresql",
                          "host": "localhost",
                          "port": 5432,
                          "database": "db-assistant",
                          "username": "user",
                          "password": "password"
                        }
                        """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connected and saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid connection details or connection failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    public ResponseEntity<DbConnectionResponse> createAndActivateConnection(
            Authentication connectedUser,
            @RequestBody DbConnectionRequest request
    ){
        try {
            User user = (User) connectedUser.getPrincipal();
            // 1. Create and save connection config
            DatabaseConnection conn = DatabaseConnection.builder()
                    .user(user)
                    .name(request.getName())
                    .dbType(request.getDbType())
                    .host(request.getHost())
                    .port(request.getPort())
                    .database(request.getDatabase())
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build();
            connectionRepo.save(conn);

            // 2. Activate immediately
            connectionManager.activateConnection(
                    connectedUser,
                    conn.getId()
            );
            DbConnectionResponse response = mapper.toDto(conn);
            return ResponseEntity.ok(response);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }

    }

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "List saved database connections",
            description = "Retrieves all database connections saved by the authenticated user. " +
                    "Does not include passwords, but contains metadata such as name, host, port, and type."
    )
    public List<DbConnectionResponse> listConnections(Authentication connectedUser) {
        return mapper.toDtoList(connectionManager.getAllConnections(connectedUser));
    }


    @PostMapping(value = "/{id}/activate", produces = "application/json")
    @Operation(
            summary = "Activate a saved database connection",
            description = "Activates a database connection previously saved by the authenticated user. "
                    + "If another connection is already active, it will be replaced."
    )
    public ResponseEntity<DbConnectionResponse> activateConnection(
            Authentication connectedUser,
            @PathVariable Integer id
    ) {
        try {
            connectionManager.activateConnection(connectedUser, id);
            User user = (User) connectedUser.getPrincipal();
            DatabaseConnection active = connectionManager.getActiveConnection(user.getId());
            DbConnectionResponse response = mapper.toDto(active);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

    @PostMapping(value = "/test", produces = "application/json")
    @Operation(
            summary = "Test a database connection",
            description = "Tests whether the provided database connection details are correct and the database is reachable."
    )
    public ResponseEntity<TestConnectionResponse> testConnection(
            @RequestBody DbConnectionRequest request,
            Authentication connectedUser
    ) {
        try {
            boolean success = connectionManager.testConnection(request);
            if (success) {
                return ResponseEntity.ok(new TestConnectionResponse(
                        "success",
                        "Connection successful"
                ));
            } else {
                return ResponseEntity.badRequest().body(new TestConnectionResponse(
                        "error",
                        "Connection failed"
                ));
            }
        } catch (DatabaseConnectionException e) {
            return ResponseEntity.badRequest().body(new TestConnectionResponse(
                    "error",
                    "Connection failed: " + e.getMessage()
            ));
        }
    }


    @DeleteMapping(value = "/{id}", produces = "application/json")
    @Operation(
            summary = "Delete a saved database connection",
            description = "Deletes a database connection previously saved by the authenticated user. "
                    + "If the connection is currently active, it will be deactivated."
    )
    public ResponseEntity<Map<String, String>> deleteConnection(
            Authentication connectedUser,
            @PathVariable Integer id
    ) {
        try {
            connectionManager.deleteConnection(connectedUser, id);
            return ResponseEntity.ok(Map.of("message", "Connection deleted successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to delete connection"));
        }
    }

    @GetMapping(value = "/active", produces = "application/json")
    @Operation(
            summary = "Get the active database connection",
            description = "Retrieves the currently active database connection for the authenticated user as a DTO."
    )
    public ResponseEntity<DbConnectionResponse> getActiveConnection(Authentication connectedUser) {
        try {
            User user = (User) connectedUser.getPrincipal();
            DatabaseConnection active = connectionManager.getActiveConnection(user.getId());
            DbConnectionResponse response = mapper.toDto(active);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(value = "/schema", produces = "application/json")
    @Operation(
            summary = "Get the database schema",
            description = "Retrieves the schema information of the currently active database connection for the authenticated user."
    )
    public ResponseEntity<SchemaResponseDto> getSchema(Authentication auth) {
        User user = (User) auth.getPrincipal();
        DataSource ds = connectionManager.getActiveDataSource(user.getId());
        String schema = schemaService.extractSchemaInfo(ds);

        return ResponseEntity.ok(new SchemaResponseDto(schema));
    }









}
