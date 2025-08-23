package com.marouane.db_assistant.message;

import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private DatabaseConnection connection;

    @Enumerated(EnumType.STRING)
    private SenderType sender; // USER or ASSISTANT

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime sentAt = LocalDateTime.now();
}
