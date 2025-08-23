package com.marouane.db_assistant.database;

import com.marouane.db_assistant.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnexionRepository extends JpaRepository<DatabaseConnection, Integer> {

    Optional<DatabaseConnection> findByIdAndUser(Integer connectionId, User user);

    List<DatabaseConnection> findAllByUser(User user);
}
