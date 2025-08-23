package com.marouane.db_assistant.sql;

import com.marouane.db_assistant.database.ConnectionManager;
import com.marouane.db_assistant.database.DatabaseConnection;
import com.marouane.db_assistant.database.SchemaService;
import com.marouane.db_assistant.message.ChatMessageRequestDto;
import com.marouane.db_assistant.message.ChatMessageService;
import com.marouane.db_assistant.message.SenderType;
import com.marouane.db_assistant.user.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class SqlGenerationService {
    private final ChatClient chatClient;
    private final SchemaService schemaService;
    private final ConnectionManager connectionManager;
    private final ChatMessageService chatMessageService;

    public SqlGenerationService(ChatClient.Builder builder, SchemaService schemaService, ConnectionManager connectionManager, ChatMessageService chatMessageService, VectorStore governanceVectorStore) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(governanceVectorStore))
                .build();
        this.schemaService = schemaService;
        this.connectionManager = connectionManager;
        this.chatMessageService = chatMessageService;
    }

    public String generateSqlFromQuestion(Authentication connectedUser, String question) {
        User user = (User) connectedUser.getPrincipal();
        DataSource dataSource = connectionManager.getActiveDataSource(user.getId());
        DatabaseConnection connection = connectionManager.getActiveConnection(user.getId());
        String schemaInfo = schemaService.extractSchemaInfo(dataSource);

        String systemPrompt = """
You are a STRICT SQL GENERATOR. Your ONLY purpose is to generate valid SQL queries based on the provided database schema.

# ABSOLUTE RULES:
1. OUTPUT ONLY RAW SQL CODE - NOTHING ELSE
2. NEVER include:
   - Explanations, comments, or notes
   - Markdown formatting (```sql```, ```, or any code blocks)
   - Prefixes like "SQL:", "Here is the query:", or any introductory text
   - Suffixes like "This query will..." or any follow-up text
   - Any non-SQL characters, words, or formatting
   - QUOTES of any kind around the SQL (no ", ', `)
3. If the question cannot be answered with SQL from the provided schema, respond with EXACTLY: "CANNOT_ANSWER"
4. NEVER invent, assume, or hallucinate tables, columns, or relationships not explicitly defined in the schema
5. NEVER provide alternative queries, multiple options, or suggestions
6. NEVER use placeholders, comments, or anything that isn't executable SQL
7. If the question is ambiguous or requires clarification, respond with "CANNOT_ANSWER"
8. If the question is not a database query (e.g., "hello", "who are you", "how are you", "thanks", general conversation), respond with "CANNOT_ANSWER"
9. If the query would violate any safety rules provided above, respond with EXACTLY: "CANNOT_ANSWER"

# OUTPUT FORMAT RULES:
- Only pure, executable SQL statements
- No line breaks before or after the SQL
- No trailing semicolons (unless required by specific SQL dialect)
- No formatting or indentation beyond what's necessary for SQL syntax
- NO QUOTES of any kind surrounding the SQL statement

# QUOTE PROHIBITION:
- NEVER wrap your response in quotes: ❌ "SELECT * FROM table"
- NEVER use backticks: ❌ `SELECT * FROM table`
- NEVER use single quotes: ❌ 'SELECT * FROM table'
- ONLY output: ✅ SELECT * FROM table

# SCHEMA INFORMATION:
%s

# EXAMPLES OF ACCEPTABLE RESPONSES:
SELECT * FROM users WHERE active = true
SELECT COUNT(*) FROM orders WHERE order_date > '2023-01-01'
CANNOT_ANSWER

# EXAMPLES OF UNACCEPTABLE RESPONSES:
"SELECT * FROM users" (Quotes are forbidden)
`SELECT * FROM users` (Backticks are forbidden)
'SELECT * FROM users' (Single quotes are forbidden)

# NON-SQL CONVERSATION EXAMPLES (MUST RETURN "CANNOT_ANSWER"):
User: "hello" → CANNOT_ANSWER
User: "hi there" → CANNOT_ANSWER  
User: "how are you?" → CANNOT_ANSWER
User: "good morning" → CANNOT_ANSWER
User: "thanks for your help" → CANNOT_ANSWER
User: "can you explain this?" → CANNOT_ANSWER
User: "who are you?" → CANNOT_ANSWER
User: "what is your name?" → CANNOT_ANSWER
User: "tell me a joke" → CANNOT_ANSWER
User: "help me" → CANNOT_ANSWER
User: "please" → CANNOT_ANSWER

# REMEMBER: YOUR OUTPUT WILL BE EXECUTED DIRECTLY AGAINST THE DATABASE. ANY NON-SQL TEXT WILL CAUSE ERRORS.
IF YOU DEVIATE FROM THESE RULES, THE DATABASE WILL FAIL AND USERS WILL BE ANGRY.
YOU ARE NOT A CHATBOT. YOU ARE AN SQL GENERATION TOOL. ONLY RESPOND TO DATABASE QUERIES.
""".formatted(schemaInfo);

       // --- Save user question ---
        chatMessageService.saveMessage(
                new ChatMessageRequestDto(SenderType.USER, question),
                connection,
                connectedUser
        );

        // --- Generate SQL with LLM ---
        String llmResponse = chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();

        // --- Save assistant response ---
        chatMessageService.saveMessage(
                new ChatMessageRequestDto(SenderType.ASSISTANT, llmResponse),
                connection,
                connectedUser
        );


        return llmResponse;
    }
}
