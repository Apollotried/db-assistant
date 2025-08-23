package com.marouane.db_assistant.message;

import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {
    public ChatMessageResponseDto toDto(ChatMessage message) {
        return new ChatMessageResponseDto(
                message.getId(),
                message.getSender(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
