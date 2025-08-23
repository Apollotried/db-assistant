package com.marouane.db_assistant.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDto {
    Integer id;
    SenderType sender;
    String content;
    LocalDateTime sentAt;
}
