package com.marouane.db_assistant.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marouane.db_assistant.message.ChatMessage;
import com.marouane.db_assistant.sql.QueryHistory;
import com.marouane.db_assistant.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseConnection {
    @Id
    @GeneratedValue
    private Integer id;

    private String name; // "Production DB", "Test DB" etc.
    private String dbType; // postgresql/mysql
    private String host;
    private int port;
    private String database;

    private String username;
    private String password;

    @OneToMany(mappedBy = "connection", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<QueryHistory> queryHistory = new ArrayList<>();

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "connection", cascade = CascadeType.REMOVE)
    private List<ChatMessage> messages;


    @Transient // Calculated field, not persisted in DB
    public String getJdbcUrl() {
        return switch (this.dbType.toLowerCase()) {
            case "postgresql" -> String.format(
                    "jdbc:postgresql://%s:%d/%s",
                    host, port, database);
            case "mysql" -> String.format(
                    "jdbc:mysql://%s:%d/%s?useSSL=false",
                    host, port, database);
            default -> throw new IllegalStateException(
                    "Unsupported DB type: " + dbType);
        };
    }


    @PrePersist @PreUpdate
    private void validate() {
        if (!List.of("postgresql", "mysql").contains(dbType.toLowerCase())) {
            throw new IllegalStateException("Invalid DB type");
        }
    }
}
