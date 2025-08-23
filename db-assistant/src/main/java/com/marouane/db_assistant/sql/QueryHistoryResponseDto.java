package com.marouane.db_assistant.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryHistoryResponseDto {
    private Integer id;
    private String query;
    private LocalDateTime queryTime;
    private QueryType queryType;
    private String connectionName;
}
