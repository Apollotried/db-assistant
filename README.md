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

### Database Management

<img width="1853" height="683" alt="c" src="https://github.com/user-attachments/assets/4da4a5fb-bb26-46e6-a402-ddc86109e4bf" />
<img width="652" height="881" alt="d" src="https://github.com/user-attachments/assets/7fb59ded-77d1-4620-a771-4fd343e961a8" />
<img width="1214" height="404" alt="e" src="https://github.com/user-attachments/assets/f153668e-0b1b-4f2e-8ffe-ffa4298da602" />


### SQL Generation Interface

<img width="1623" height="894" alt="f" src="https://github.com/user-attachments/assets/290bc7f5-e8e7-402b-bb77-70133aac1524" />
<img width="1004" height="850" alt="g" src="https://github.com/user-attachments/assets/678cb855-ab08-4d8c-8264-44cea2662728" />
<img width="1546" height="493" alt="h" src="https://github.com/user-attachments/assets/3bee4855-67e6-4e23-a632-8aaf7826dadb" />


<img width="912" height="443" alt="i" src="https://github.com/user-attachments/assets/7002c9b4-ad8a-4125-b4f7-9c960bb0d0b0" />
<img width="1493" height="386" alt="j" src="https://github.com/user-attachments/assets/7d891b93-c017-4705-b729-1c50e2cabb00" />


### Results & History

<img width="1595" height="621" alt="k" src="https://github.com/user-attachments/assets/4b618602-f1b3-43a2-b748-5e7c7269ec91" />




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
