package com.marouane.db_assistant.message;

import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper mapper;

    public ChatMessageResponseDto saveMessage(
            ChatMessageRequestDto request,
            DatabaseConnection connection,
            Authentication connectedUser
    ) {
        User user = (User) connectedUser.getPrincipal();
        ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setConnection(connection);
        message.setSender(request.getSender());
        message.setContent(request.getContent());

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return mapper.toDto(savedMessage);
    }

    public List<ChatMessageResponseDto> getMessagesByConnection(Authentication connectedUser, DatabaseConnection connection) {
        User user = (User) connectedUser.getPrincipal();

        return chatMessageRepository.findByUserAndConnectionOrderBySentAtAsc(user, connection).stream()
                .map(mapper::toDto)
                .toList();
    }

    public void deleteMessagesByConnection(Authentication auth, DatabaseConnection connection) {
        User user = (User) auth.getPrincipal();
        List<ChatMessage> messages = chatMessageRepository.findByUserAndConnectionOrderBySentAtAsc(user, connection);
        chatMessageRepository.deleteAll(messages);
    }

}
