package com.marouane.db_assistant.sql;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sql")
@RequiredArgsConstructor
@Tag(name = "SQL Operations", description = "Endpoints to generate and execute SQL queries")
public class SqlController {
    private final SqlExecutionService sqlExecutionService;
    private final SqlGenerationService sqlGenerationService;

    @PostMapping(value = "/generate", produces = "application/json")
    @Operation(
            summary = "Generate SQL from a natural language question",
            description = "Takes a plain-language question and converts it into an SQL query using the active database connection.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Natural language question to generate SQL",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Show all customers who spent more than 1000 last month\"")
                    )
            )
    )
    public ResponseEntity<SqlResponseDto> generateSql(@RequestBody String question, Authentication connectedUser) {
        try {
            String sql = sqlGenerationService.generateSqlFromQuestion(connectedUser, question);
            return ResponseEntity.ok(new SqlResponseDto(sql));
        } catch (IllegalStateException e) {
            // Wrap error in the DTO
            SqlResponseDto errorDto = new SqlResponseDto(null);
            errorDto.setError("No active database connection. Please connect to a database first.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        } catch (Exception e) {
            SqlResponseDto errorDto = new SqlResponseDto(null);
            errorDto.setError("Error generating SQL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
        }
    }

    @PostMapping(value = "/execute", produces = "application/json")
    @Operation(
            summary = "Execute a raw SQL query",
            description = "Runs the given SQL query on the active database connection and returns structured results.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "SQL query string to execute on the active database connection",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SqlRequest.class),
                            examples = @ExampleObject(
                                    name = "Select example",
                                    value = "{\"sql\": \"SELECT * FROM customers WHERE total_spent > 1000\"}"
                            )
                    )
            )
    )
    public QueryResultDto executeSql(@RequestBody SqlRequest sqlRequest, Authentication connectedUser) {
        try {
            if (sqlRequest == null || sqlRequest.getSql() == null || sqlRequest.getSql().trim().isEmpty()) {
                return new QueryResultDto(false, "SQL query cannot be empty");
            }

            String sql = sqlRequest.getSql().trim();
            return sqlExecutionService.execute(connectedUser, sql);

        } catch (IllegalArgumentException e) {
            // No active database connection
            return new QueryResultDto(false, "No database connected: " + e.getMessage());

        } catch (Exception e) {
            // Catch any other unexpected errors
            return new QueryResultDto(false, "Unexpected error: " + e.getMessage());
        }
    }





}
