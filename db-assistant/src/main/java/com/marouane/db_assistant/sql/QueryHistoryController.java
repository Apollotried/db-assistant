package com.marouane.db_assistant.sql;

import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("history")
@RequiredArgsConstructor
@Tag(name = "Query History", description = "Retrieve query execution history for the authenticated user")
public class QueryHistoryController {
    private final QueryHistoryService queryHistoryService;

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "Get query execution history",
            description = "Retrieves the query execution history for the authenticated user. " +
                    "Includes details such as query text, execution time, query type, and connection name."
    )
    public ResponseEntity<List<QueryHistoryResponseDto>> queryHistory(
            Authentication connectedUser
    ){
        try{
            List<QueryHistoryResponseDto> history = queryHistoryService.getQueryHistory(connectedUser);
            return ResponseEntity.ok(history);
        }catch(IllegalStateException  e){
            return ResponseEntity.badRequest().body(List.of());
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    private boolean isConnectionOwnedByUser(DatabaseConnection connection, User user) {
        return connection.getUser() != null && connection.getUser().getId().equals(user.getId());
    }
}
