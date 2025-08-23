package com.marouane.db_assistant.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IngestionService implements CommandLineRunner {

    private final VectorStore vectorStore;

    @Value("classpath:/docs/sql_governance_rules.md")
    private Resource rulesDocument;


    @Override
    public void run(String... args) throws Exception {
        // Check if rules already exist in vector store
        var existingDocs = vectorStore.similaritySearch("SQL Governance Rules");

        if (!existingDocs.isEmpty()) {
            log.info("SQL Governance Rules already ingested, skipping.");
            return;
        }

        var textReader = new TextReader(rulesDocument);
        TextSplitter textSplitter = new TokenTextSplitter();

        vectorStore.accept(textSplitter.apply(textReader.get()));
        log.info("SQL Governance Rules ingested into VectorStore!");
    }
}
