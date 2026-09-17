# 🚀 Spring Blog API Setup Guide

## 📋 Prerequisites Check

Run the setup script to check what's installed:

```bash
# Option 1: PowerShell (Recommended)
.\setup.ps1

# Option 2: Batch file
.\setup.bat
```

## 🔧 Installation Instructions

### 1. Java Installation (Required)

#### **Download Java 21:**
- **Oracle JDK:** https://www.oracle.com/java/technologies/downloads/
- **Microsoft OpenJDK:** https://learn.microsoft.com/en-us/java/openjdk/download

#### **Installation Steps:**
1. Download Windows x64 installer
2. Run the installer with Administrator privileges
3. Follow the installation wizard
4. Set JAVA_HOME environment variable (usually automatic)

#### **Verify Installation:**
```bash
java -version
```

### 2. PostgreSQL Installation (Required for Local Development)

#### **Download PostgreSQL:**
- **Official:** https://www.postgresql.org/download/windows/

#### **Installation Steps:**
1. Download the Windows installer
2. Run the installer
3. Set password: `postgres` (recommended for this project)
4. Keep default port: `5432`
5. Install pgAdmin 4 (GUI tool)
6. Complete installation

#### **Create Database:**
```sql
-- Using pgAdmin or SQL Shell
CREATE DATABASE spring_blog_db;
```

#### **Verify Installation:**
```bash
psql --version
psql -U postgres -d spring_blog_db -h localhost
```

### 3. Docker Installation (Optional but Recommended)

#### **Download Docker Desktop:**
- **Official:** https://www.docker.com/products/docker-desktop/

#### **Installation Steps:**
1. Download Docker Desktop for Windows
2. Run the installer
3. Restart your computer
4. Start Docker Desktop
5. Wait for Docker to be ready

#### **Verify Installation:**
```bash
docker --version
docker-compose --version
```

## ⚙️ Configuration Setup

### 1. Environment Variables

```bash
# Copy sample environment file
copy .env.sample .env
```

### 2. Update .env File

Edit `.env` file with your credentials:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/spring_blog_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

SPRING_MAIL_HOST=localhost
SPRING_MAIL_PORT=1025
SPRING_MAIL_USERNAME=mailuser
SPRING_MAIL_PASSWORD=mailpass

JWT_SECRET_KEY=yQw1nQw8w6v7v8QkQk9pQk3vQk2pQk1nQk0pQk9nQk8pQk7vQk6pQk5nQk4pQk3v
JWT_EXPIRATION_TIME=3600000

CLIENT_APP_URL=http://localhost:3000
```

## 🚀 Running the Application

### **Option 1: Docker Compose (Recommended)**

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

**Services Started:**
- PostgreSQL (port 5432)
- pgAdmin (port 5050) - http://localhost:5050
- MailHog (port 8025) - http://localhost:8025
- Spring Boot API (port 8082) - http://localhost:8082/api/v1/

### **Option 2: Local Development**

```bash
# Build the project
.\gradlew.bat build

# Run the application
.\gradlew.bat bootRun
```

**Access Points:**
- API: http://localhost:8082/api/v1/
- Swagger UI: http://localhost:8082/api/v1/swagger-ui/index.html

## 🔍 Troubleshooting

### **Java Version Issues**
```
Error: Unsupported class file major version 69
```
**Solution:** Install Java 21 and update JAVA_HOME

### **PostgreSQL Connection Issues**
```
Error: Connection refused
```
**Solution:**
1. Check PostgreSQL service is running
2. Verify port 5432 is not blocked
3. Check credentials in .env file

### **Gradle Build Issues**
```
Error: Build failed
```
**Solution:**
```bash
# Clean and rebuild
.\gradlew.bat clean build
```

### **Port Already in Use**
```
Error: Port 8082 is already in use
```
**Solution:**
```bash
# Find process using port 8082
netstat -ano | findstr :8082

# Kill the process
taskkill /PID <PID> /F
```

## ✅ Verification

### **Test API Health:**
```bash
curl http://localhost:8082/api/v1/
```

### **Test Authentication:**
```bash
# Register user
curl -X POST http://localhost:8082/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","username":"testuser","email":"test@example.com","password":"Password123!"}'

# Login
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Password123!"}'
```

## 🎯 Next Steps

1. ✅ Run setup script to check prerequisites
2. ✅ Install missing components
3. ✅ Configure environment variables
4. ✅ Start the application
5. ✅ Test API endpoints
6. ✅ Access Swagger UI for API documentation

## 📚 Additional Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/
- **Docker Documentation:** https://docs.docker.com/
- **JWT Authentication:** https://jwt.io/

## 🆘 Support

If you encounter issues:
1. Check the logs in `logs/spring-blog-api.log`
2. Review this setup guide
3. Check the project README.md
4. Open an issue on GitHub