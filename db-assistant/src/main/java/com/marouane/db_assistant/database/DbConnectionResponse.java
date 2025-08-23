package com.marouane.db_assistant.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DbConnectionResponse {
    Integer id;
    String name;
    String dbType;
    String host;
    int port;
    String database;
    String username;
}
