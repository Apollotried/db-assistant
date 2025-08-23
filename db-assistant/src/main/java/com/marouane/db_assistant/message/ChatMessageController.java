package com.marouane.db_assistant.message;

import com.marouane.db_assistant.database.ConnectionManager;
import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ConnectionManager connectionManager;
    private final ChatMessageService chatMessageService;

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "Get chat messages",
            description = "Retrieves all chat messages for the currently active database connection of the authenticated user."
    )
    public List<ChatMessageResponseDto> getMessages(Authentication auth) {
        User user = (User) auth.getPrincipal();
        DatabaseConnection connection = connectionManager.getActiveConnection(user.getId());

        return chatMessageService.getMessagesByConnection(auth, connection);
    }

    @DeleteMapping(produces = "application/json")
    @Operation(
            summary = "Clear chat messages",
            description = "Deletes all chat messages for the currently active database connection of the authenticated user."
    )
    public ResponseEntity<Void> clearChat(Authentication auth) {
        DatabaseConnection connection = connectionManager.getActiveConnection(((User) auth.getPrincipal()).getId());
        chatMessageService.deleteMessagesByConnection(auth, connection);
        return ResponseEntity.noContent().build();
    }

}
