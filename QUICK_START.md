# ⚡ Quick Start Guide

## 🎯 Fastest Way to Run (3 Steps)

### **Step 1: Install Docker Desktop**
Download: https://www.docker.com/products/docker-desktop/

### **Step 2: Run Docker Compose**
```bash
cd "C:\Users\DELL\Desktop\Aayush\spring-blog-api"
docker-compose up -d
```

### **Step 3: Access API**
- API: http://localhost:8082/api/v1/
- Swagger: http://localhost:8082/api/v1/swagger-ui/index.html

---

## 🖥️ Local Development (5 Steps)

### **Step 1: Install Java 21**
Download: https://www.oracle.com/java/technologies/downloads/

### **Step 2: Install PostgreSQL**
Download: https://www.postgresql.org/download/windows/
Password: `postgres`
Database: `spring_blog_db`

### **Step 3: Run Setup Script**
```bash
.\setup.ps1
```

### **Step 4: Update .env File**
Already created! Just verify credentials:
```env
SPRING_DATASOURCE_PASSWORD=postgres
```

### **Step 5: Run Application**
```bash
.\gradlew.bat bootRun
```

---

## 🔧 What I've Set Up For You

✅ **Authentication Caching Fix** - Multiple logins will work now
✅ **Setup Scripts** - Auto-check your environment
✅ **Environment Files** - .env file created from sample
✅ **Configuration** - Security config updated
✅ **Documentation** - Complete setup guide

---

## 🚀 Ready to Test

Once running, test the authentication fix:

```bash
# Register user
curl -X POST http://localhost:8082/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","username":"testuser","email":"test@example.com","password":"Password123!"}'

# Login first time
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Password123!"}'

# Login second time (should work now!)
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Password123!"}'
```

---

## 📖 Detailed Guide

See `SETUP_GUIDE.md` for complete instructions.