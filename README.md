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

## 📋 How It Works

1. **Connect**: User provides database connection details (host, port, username, password, DB type)
2. **Ask**: User types natural language questions (e.g., "Show me the top 10 customers by total spending in 2024")
3. **Generate**: Spring AI generates SQL queries based on database schema
4. **Review**: User can approve, edit, or reject generated SQL before execution
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

## 📦 Installation

### Prerequisites
- Java 21+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.6+

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
