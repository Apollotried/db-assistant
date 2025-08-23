package com.marouane.db_assistant.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {

    @Bean
    public PgVectorStore pgVectorStore(EmbeddingModel embeddingModel) {
        HikariDataSource vectorDataSource = new HikariDataSource();
        vectorDataSource.setJdbcUrl("jdbc:postgresql://localhost:5434/vector_store");
        vectorDataSource.setUsername("user");
        vectorDataSource.setPassword("password");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(vectorDataSource);

        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .initializeSchema(true)
                .build();
    }

}
