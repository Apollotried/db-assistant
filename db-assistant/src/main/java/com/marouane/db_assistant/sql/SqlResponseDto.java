package com.marouane.db_assistant.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SqlResponseDto {
    private String sql;
    private String error;

    public SqlResponseDto(String sql) {
        this.sql = sql;
    }
}
