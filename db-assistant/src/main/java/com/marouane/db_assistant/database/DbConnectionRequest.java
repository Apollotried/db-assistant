package com.marouane.db_assistant.database;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DbConnectionRequest {
    private String name;
    private String dbType;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
}
