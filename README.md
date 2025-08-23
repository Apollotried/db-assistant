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


## 📸 Application Screenshots

### Authentication Pages

<img width="1163" height="542" alt="a" src="https://github.com/user-attachments/assets/4b7c94b9-4c5c-46ed-a5c5-0aeb44445282" />


<img width="1021" height="635" alt="b" src="https://github.com/user-attachments/assets/6d4a3361-d4b0-4640-a350-b7ab94edcd27" />

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
