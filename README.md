# Database Assistant

A full-stack application that converts natural language questions to SQL queries with AI-powered safety controls and RAG-based governance.

## 🚀 Features

### Core Functionality
- **🔗 Connect to Your Database** - Support for PostgreSQL, MySQL with secure connection management
- **💬 Natural Language to SQL** - Convert plain English questions to executable SQL queries
- **🛡️ AI-Powered Safety** - RAG-based governance prevents dangerous queries
- **👥 Multi-User Support** - JWT authentication with individual workspaces
- **💾 Query History** - Save and reuse successfully executed queries

### Technical Features
- **Spring Boot Backend** with OpenAPI documentation
- **Angular Frontend** with Bootstrap UI
- **Spring AI Integration** for SQL generation
- **RAG (Retrieval Augmented Generation)** for safety rules enforcement
- **JWT Authentication** with Spring Security
- **Automated Service Generation** from OpenAPI specs
- Dockerized Infrastructure for databases and vector storage

## 📋 How It Works

1. **Connect**: User provides database connection details (host, port, username, password, DB type)
2. **Ask**: User types natural language questions (e.g., "Show me the top 10 customers by total spending in 2024")
3. **Generate**: Spring AI generates SQL queries based on database schema
4. **Review**: User can approve, or reject generated SQL before execution
5. **Execute**: Approved queries run against the connected database
6. **Save**: Successfully executed queries are saved for future reuse

## 🛡️ Safety Features

- **RAG-Based Governance**: Retrieval Augmented Generation provides real-time safety rules
- **Query Validation**: Prevents destructive operations (DROP TABLE, DELETE without WHERE)
- **Schema Awareness**: Only generates queries for existing tables and columns
- **Manual Approval**: All queries require user confirmation before execution

## 🏗️ Architecture

### Backend (Spring Boot)
- **Spring Web MVC**: REST API endpoints
- **Spring Security**: JWT authentication and authorization
- **Spring AI**: Natural language to SQL conversion
- **Spring Data JPA**: Database operations
- **PgVector**: Vector store for RAG functionality
- **OpenAPI**: Automatic API documentation

### Frontend (Angular)
- **Angular Framework**: Single-page application
- **Bootstrap**: Responsive UI components
- **JWT Interceptors**: Authentication handling
- **RxJS**: Reactive programming patterns

### Infrastructure (Dockerized)
- **PostgreSQL**: Application database
- **PgVector**: Extension-enabled PostgreSQL instance for embeddings
- **Docker Compose**: Orchestration of multiple containers
- **Persistence**: Named volumes for database data

## 📦 Installation

### Prerequisites
- Java 21+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.6+
- Docker + Docker Compose

### Backend Setup
```bash
cd backend

# Copy environment template
cp src/main/resources/application.properties.template src/main/resources/application-local.properties

# Set your environment variables
export OPENAI_API_KEY=your_openai_api_key
export JWT_SECRET_KEY=your_jwt_secret_key

# Run the application
./mvnw spring-boot:run



# SQL Governance Rules for AI Assistant

## 1. SAFETY & DESTRUCTIVE OPERATIONS (Prevent data loss/damage)

1.  **NEVER** generate a `DELETE` or `UPDATE` statement without a `WHERE` clause.
    - Prevents mass deletion or modification of entire tables.

2.  **ALWAYS** include a `WHERE` clause that is specific and non-trivial in destructive operations.
    - Reduces risk of accidentally affecting far more rows than intended.

3.  **NEVER** generate `TRUNCATE` statements.
    - Prevents irreversible, full-table data deletion without logging.

4.  **NEVER** generate `DROP` statements unless explicitly requested with object confirmation.
    - Prevents permanent loss of tables, schemas, or databases.

5.  **NEVER** include `CASCADE` in `DROP` or `DELETE` statements.
    - Avoids unintended removal of dependent objects or child rows.

6.  **ENSURE** destructive operations (`DELETE`, `UPDATE`, `DROP`) are always scoped by schema and table names.
    - Prevents ambiguity and accidental targeting of the wrong objects.

7.  **AVOID** generating `INSERT` statements without explicit column lists.
    - Protects against schema drift issues where column order changes over time.

8.  **ALWAYS** wrap potentially destructive multi-step operations in transactions.
    - Ensures the possibility of rollback in case of errors.

9.  **NEVER** generate DDL (`CREATE`, `ALTER`, `DROP`) in response to analytical or reporting questions.
    - Prevents schema-level modifications when only data retrieval is needed.

10. **NEVER** generate statements that modify system tables or system schemas.
    - Avoids corruption or disruption of database internals.

## 2. PERFORMANCE & EFFICIENCY (Prevent slow/costly queries)

1.  **AVOID** using `SELECT *` in queries.
    - Reduces unnecessary I/O and improves query performance.

2.  **ALWAYS** filter rows with `WHERE` clauses when possible.
    - Prevents scanning entire tables unnecessarily.

3.  **PREFER** indexed columns in `JOIN` and `WHERE` conditions.
    - Improves lookup speed and reduces full table scans.

4.  **AVOID** using functions on columns in `WHERE` clauses when possible.
    - Prevents index usage and forces full scans.

5.  **ENSURE** use of `LIMIT` (or vendor equivalent) in queries that may return large result sets.
    - Reduces memory usage and network load.

6.  **NEVER** use `ORDER BY` without `LIMIT` in large-table queries unless explicitly required.
    - Prevents sorting overhead on huge datasets.

7.  **PREFER** `EXISTS` over `IN` for subqueries with potentially large result sets.
    - Provides better performance in most engines.

8.  **ALWAYS** aggregate only necessary columns in `GROUP BY`.
    - Reduces sorting and memory usage costs.

9.  **AVOID** nested subqueries when `JOIN`s can achieve the same result.
    - Simplifies execution plans and improves optimization.

10. **ENSURE** join conditions use explicit equality or appropriate predicates.
    - Prevents Cartesian joins that explode row counts.

11. **NEVER** generate `CROSS JOIN` unless explicitly required.
    - Avoids unintended multiplicative results.

12. **PREFER** CTEs (`WITH` clauses) or temporary tables for complex repeated subqueries.
    - Improves readability and optimizer efficiency.

13. **ALWAYS** ensure `UNION` is used only when deduplication is required; otherwise use `UNION ALL`.
    - Saves expensive deduplication when unnecessary.

14. **AVOID** wildcards in `LIKE` searches at the beginning of strings.
    - Prevents disabling of indexes and causing full scans.

15. **NEVER** use `DISTINCT` without checking if duplicates are possible.
    - Prevents unnecessary sorting and deduplication cost.

## 3. CORRECTNESS & SYNTAX (Prevent errors and bugs)

1.  **ALWAYS** fully qualify table names with schema if multiple schemas exist.
    - Prevents ambiguity and incorrect object references.

2.  **ENSURE** column aliases are unique in the `SELECT` clause.
    - Avoids confusion and parsing errors in client applications.

3.  **ALWAYS** match column counts and data types in `UNION` queries.
    - Prevents syntax and runtime errors.

4.  **NEVER** rely on implicit type casting; explicitly cast when mixing data types.
    - Prevents unexpected conversion errors across databases.

5.  **ENSURE** aggregate functions are paired with appropriate `GROUP BY` clauses.
    - Prevents errors in strict SQL modes.

6.  **NEVER** use reserved keywords as identifiers without quoting.
    - Prevents syntax errors across different SQL dialects.

7.  **ALWAYS** alias derived tables and subqueries.
    - Prevents parsing errors and improves clarity.

8.  **ENSURE** `JOIN` conditions reference valid keys in both tables.
    - Prevents accidental cross joins or meaningless results.

9.  **AVOID** implicit joins (comma-separated tables); always use explicit `JOIN` syntax.
    - Ensures correctness and prevents accidental Cartesian products.

10. **ENSURE** Boolean logic uses explicit `AND`/`OR` grouping with parentheses when mixing.
    - Prevents operator-precedence mistakes.

11. **ALWAYS** specify explicit sort order (`ASC`/`DESC`) in `ORDER BY`.
    - Prevents relying on vendor defaults.

## 4. CLARITY & MAINTAINABILITY (Ensure readability)

1.  **ALWAYS** use clear, descriptive aliases for tables and columns.
    - Improves query readability and debugging.

2.  **PREFER** formatting SQL with line breaks for `SELECT`, `FROM`, `WHERE`, `GROUP BY`, `ORDER BY`.
    - Enhances maintainability and review.

3.  **ENSURE** consistent capitalization of SQL keywords.
    - Improves readability and avoids confusion.

4.  **NEVER** embed business logic in overly complex nested queries; use views or CTEs.
    - Keeps queries understandable and maintainable.

5.  **ALWAYS** comment non-trivial queries with intent or reasoning.
    - Helps future readers understand query purpose.

6.  **PREFER** explicit column lists over ambiguous expressions.
    - Improves clarity and prevents schema drift issues.
