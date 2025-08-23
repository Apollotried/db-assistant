package com.marouane.db_assistant.message;

import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByUserAndConnectionOrderBySentAtAsc(User user, DatabaseConnection connection);
}
