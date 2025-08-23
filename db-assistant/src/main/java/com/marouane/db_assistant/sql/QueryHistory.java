package com.marouane.db_assistant.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "query_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryHistory {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private DatabaseConnection connection;

    private String query;

    private LocalDateTime queryTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private QueryType queryType;
}
