package com.marouane.db_assistant.sql;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueryHistoryMapper {

    public QueryHistoryResponseDto toResponseDto(QueryHistory queryHistory) {
        if (queryHistory == null) {
            return null;
        }

        QueryHistoryResponseDto dto = new QueryHistoryResponseDto();
        dto.setId(queryHistory.getId());
        dto.setQuery(queryHistory.getQuery());
        dto.setQueryTime(queryHistory.getQueryTime());
        dto.setQueryType(queryHistory.getQueryType());

        // Safely get connection name
        if (queryHistory.getConnection() != null) {
            dto.setConnectionName(queryHistory.getConnection().getName());
        } else {
            dto.setConnectionName("Unknown Connection");
        }

        return dto;
    }

    public List<QueryHistoryResponseDto> toResponseDtoList(List<QueryHistory> queryHistories) {
        if (queryHistories == null) {
            return Collections.emptyList();
        }

        return queryHistories.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}
