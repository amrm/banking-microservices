# 🏦 Banking Microservices System

A modern, scalable banking transfer system built with Java 21 LTS microservices and Angular 21 frontend. Features real-time transfers, workflow approvals, and comprehensive audit logging.

**Repository:** https://github.com/amrm/banking-microservices

---

## 🚀 Quick Start

### Clone the Repository

```bash
git clone https://github.com/amrm/banking-microservices.git
cd banking-microservices
```

Or using SSH (if you have SSH keys configured):

```bash
git clone git@github.com:amrm/banking-microservices.git
cd banking-microservices
```

---

## 📋 System Overview

### Backend Architecture (Java 21 LTS)
- **7 Microservices** with independent databases
- **Spring Boot 3.3.9** - Latest stable for Java 21
- **Spring Data JPA** - Database abstraction
- **Spring WebFlux** - Reactive programming support
- **Spring Security** - Authentication & Authorization
- **Neo4j** - Graph database for relationship analysis
- **PostgreSQL** - Relational data storage
- **Resilience4j** - Circuit breakers & retry logic

### Frontend (Angular 21)
- **Standalone Components** - Modern Angular architecture
- **HttpClient** - REST API communication
- **RxJS** - Reactive programming
- **Functional Interceptors** - Request/Response middleware
- **Auth Guard** - Route protection
- **TypeScript 5.9** - Type-safe development

---

## 📦 Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 21 LTS | Runtime environment |
| Spring Boot | 3.3.9 | Microservices framework |
| Spring Framework | 6.1.17 | Core framework |
| PostgreSQL | 16 | Relational database |
| Neo4j | 5.14 | Graph database |
| Angular | 21 | Frontend framework |
| TypeScript | 5.9 | Frontend language |
| Maven | 3.9+ | Build tool |
| Node.js | 18+ | Frontend build environment |

---

## 🏗️ Project Structure

```
banking-microservices/
├── account-service/              # Account management
├── api-gateway/                  # API Gateway
├── core-banking-service/         # Core banking operations
├── limit-service/                # Transfer limits
├── transfer-service/             # Transfer orchestration
├── user-service/                 # User management & authentication
├── workflow-service/             # Approval workflows
├── banking-ui/                   # Angular frontend
├── docker-compose.yml            # Database setup
└── README.md                      # This file
```

---

## 🔧 Prerequisites

### System Requirements
- **Java 21 LTS** or higher
- **Node.js 18+** (for frontend)
- **Docker & Docker Compose** (recommended for databases)
- **Maven 3.9+**
- **PostgreSQL 16** (or use Docker)
- **Neo4j 5.14** (or use Docker)

### Install Java 21

**macOS:**
```bash
brew install java21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

**Windows:**
Download from [Oracle](https://www.oracle.com/java/technologies/downloads/#java21) or use chocolatey:
```bash
choco install openjdk21
```

### Verify Installation
```bash
java -version
mvn -version
node -v
npm -v
```

---

## 🗄️ Database Setup

### Option 1: Docker Compose (Recommended)

```bash
cd banking-microservices
docker-compose up -d
```

This starts:
- PostgreSQL (port 5432)
- Neo4j (port 7687)
- Elasticsearch (port 9200) - optional
- Zipkin (port 9411) - optional

### Option 2: Manual Installation

**PostgreSQL:**
```bash
# macOS
brew install postgresql@16
brew services start postgresql@16

# Linux
sudo apt install postgresql-16 postgresql-contrib
sudo systemctl start postgresql
```

**Neo4j:**
```bash
# macOS
brew install neo4j
neo4j start

# Linux
wget https://neo4j.com/artifact.php?name=neo4j-community-5.14.0-unix.tar.gz
tar -xzf neo4j-community-5.14.0-unix.tar.gz
./neo4j-community-5.14.0/bin/neo4j start
```

### Initialize Databases

**PostgreSQL Setup:**
```bash
psql -U postgres -f "# Complete Database Setup Guide"
```

**Neo4j Setup:**
Open Neo4j Browser (http://localhost:7474) and run the scripts from the database setup guide.

---

## 🚀 Running the Application

### Backend Services

Each service runs independently on its own port:

```bash
# Terminal 1 - User Service (port 8001)
cd user-service
./mvnw spring-boot:run

# Terminal 2 - Account Service (port 8002)
cd account-service
./mvnw spring-boot:run

# Terminal 3 - Transfer Service (port 8003)
cd transfer-service
./mvnw spring-boot:run

# Terminal 4 - Core Banking Service (port 8004)
cd core-banking-service
./mvnw spring-boot:run

# Terminal 5 - Limit Service (port 8005)
cd limit-service
./mvnw spring-boot:run

# Terminal 6 - Workflow Service (port 8006)
cd workflow-service
./mvnw spring-boot:run

# Terminal 7 - API Gateway (port 8080)
cd api-gateway
./mvnw spring-boot:run
```

### Frontend Application

```bash
cd banking-ui
npm install
npm start
```

Frontend runs on: http://localhost:4200

### Access Points

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:4200 | User interface |
| API Gateway | http://localhost:8080 | API entry point |
| Neo4j Browser | http://localhost:7474 | Graph database UI |
| PostgreSQL | localhost:5432 | Database connection |

---

## 🧪 Testing

### Run All Tests

```bash
# Test all microservices
for service in account-service api-gateway core-banking-service limit-service transfer-service user-service workflow-service; do
  echo "Testing $service..."
  cd "$service"
  ./mvnw clean test
  cd ..
done
```

### Test Individual Service

```bash
cd transfer-service
./mvnw clean test
```

### Test Results
All 7 services have passing tests:
- ✅ account-service - 1 test passing
- ✅ api-gateway - 1 test passing
- ✅ core-banking-service - 1 test passing
- ✅ limit-service - 1 test passing
- ✅ transfer-service - 1 test passing
- ✅ user-service - 1 test passing
- ✅ workflow-service - 1 test passing

---

## 🏗️ Building for Production

### Backend Build

```bash
# Build all services
for service in account-service api-gateway core-banking-service limit-service transfer-service user-service workflow-service; do
  cd "$service"
  ./mvnw clean package -DskipTests
  cd ..
done
```

### Frontend Build

```bash
cd banking-ui
npm run build
# Output: dist/banking-ui/
```

---

## 📚 API Documentation

### User Service (Port 8001)
```bash
POST   /api/users/register          # Register new user
POST   /api/users/login             # User login
GET    /api/users/{userId}          # Get user details
PUT    /api/users/{userId}          # Update user
```

### Account Service (Port 8002)
```bash
POST   /api/accounts                # Create account
GET    /api/accounts/{accountId}    # Get account
GET    /api/accounts/user/{userId}  # List user accounts
PUT    /api/accounts/{accountId}    # Update account
```

### Transfer Service (Port 8003)
```bash
POST   /api/transfers/initiate      # Initiate transfer
GET    /api/transfers/{transferId}  # Get transfer
GET    /api/transfers/user/{userId} # List user transfers
```

### Core Banking Service (Port 8004)
```bash
POST   /api/core-banking/process    # Process transaction
GET    /api/core-banking/status     # Check transaction status
```

### Limit Service (Port 8005)
```bash
GET    /api/limits/{userId}         # Get user limits
PUT    /api/limits/{limitId}        # Update limit
POST   /api/limits/check            # Check limit availability
```

### Workflow Service (Port 8006)
```bash
GET    /api/workflows/{workflowId}  # Get workflow
PUT    /api/workflows/{workflowId}  # Update workflow status
POST   /api/workflows/approve       # Approve transfer
```

### API Gateway (Port 8080)
```bash
# Routes all requests to microservices
GET    /api/**                      # Forward to appropriate service
```

---

## 🔐 Authentication

### Default Credentials

```
Username: john.doe
Password: password123
Email: john.doe@bank.com

Username: jane.smith
Password: password123
Email: jane.smith@bank.com

Username: admin
Password: password123
Email: admin@bank.com
```

### Authentication Flow
1. User logs in via `/api/users/login`
2. JWT token returned in response
3. Include token in Authorization header: `Authorization: Bearer <token>`
4. All subsequent requests use this token

---

## 📊 Database Schema

### PostgreSQL Databases
- **userdb** - Users and authentication
- **accountdb** - Account information and balance history
- **transferdb** - Transfer records and status history
- **limitdb** - User limits and utilization tracking
- **workflowdb** - Workflow approvals and actions
- **corebankingdb** - Core banking transactions

### Neo4j Graph Model
- **User** nodes - User entities
- **Account** nodes - Account entities
- **Transfer** nodes - Transfer relationships
- **OWNS** relationships - User owns accounts
- **TRANSACTED_WITH** relationships - Account transaction history

---

## 🐛 Troubleshooting

### Java 21 Compatibility Issues
```bash
# Ensure correct Java version
java -version
# Should show "Java 21" or higher

# Set JAVA_HOME if needed
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process if needed
kill -9 <PID>
```

### Database Connection Issues
```bash
# Check PostgreSQL is running
psql -U postgres -d userdb -c "SELECT version();"

# Check Neo4j is running
curl http://localhost:7474/
```

### Build Failures
```bash
# Clean and rebuild
./mvnw clean install

# Skip tests if needed
./mvnw clean install -DskipTests

# Check dependency conflicts
./mvnw dependency:tree
```

---

## 📈 Performance & Scalability

### Key Features
- **Circuit Breaker** - Prevent cascading failures
- **Retry Logic** - Automatic retry with exponential backoff
- **Connection Pooling** - HikariCP for database connections
- **Caching** - Spring Cache abstraction ready
- **Async Processing** - WebFlux for non-blocking I/O
- **Load Balancing** - API Gateway for routing

### Monitoring
- **Logging** - SLF4J with Logback
- **Tracing** - Zipkin support (optional)
- **Metrics** - Micrometer integration (optional)

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📞 Support

For issues and questions:
- Open an issue on [GitHub Issues](https://github.com/amrm/banking-microservices/issues)
- Check existing documentation
- Review test cases for usage examples

---

## 🎯 Version History

### v1.0.0 (Current)
- ✅ Java 21 LTS migration complete
- ✅ Spring Boot 3.3.9 stable version
- ✅ 7 microservices with passing tests
- ✅ Angular 21 frontend with successful build
- ✅ PostgreSQL 16 & Neo4j 5.14 integration
- ✅ Complete API documentation

---

**Last Updated:** December 6, 2025

**Repository:** https://github.com/amrm/banking-microservices
