package com.marouane.db_assistant.exception;

public class DatabaseConnectionException extends RuntimeException {
  public DatabaseConnectionException(String message) {
    super(message);
  }
}
