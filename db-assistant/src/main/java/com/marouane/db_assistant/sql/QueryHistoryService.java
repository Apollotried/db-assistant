package com.marouane.db_assistant.sql;

import com.marouane.db_assistant.database.ConnectionManager;
import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class QueryHistoryService {
    private final QueryHistoryRepository queryHistoryRepository;
    private final QueryHistoryMapper queryHistoryMapper;
    private final ConnectionManager connectionManager;


    public void logQuery(User user, DatabaseConnection connection, String query) {
        QueryHistory history = new QueryHistory();
        history.setUser(user);
        history.setConnection(connection);
        history.setQuery(query);
        history.setQueryType(detectQueryType(query));
        queryHistoryRepository.save(history);
    }

    private QueryType detectQueryType(String sql) {
        String firstWord = sql.trim().split("\\s+")[0].toUpperCase();
        try {
            return QueryType.valueOf(firstWord);
        } catch (Exception e) {
            return QueryType.OTHER;
        }
    }


    public List<QueryHistoryResponseDto> getQueryHistory(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        DatabaseConnection activeConnection = connectionManager.getActiveConnection(user.getId());
        List<QueryHistory> history = queryHistoryRepository.findByUserAndConnectionOrderByQueryTimeDesc(user, activeConnection);
        return queryHistoryMapper.toResponseDtoList(history);
    }
}
