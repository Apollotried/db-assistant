package com.marouane.db_assistant.sql;

import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, Integer> {

    List<QueryHistory> findByUserAndConnectionOrderByQueryTimeDesc(User user, DatabaseConnection connection);
}
