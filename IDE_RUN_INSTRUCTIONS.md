# 🖥️ IDE-based Running Instructions

Since Gradle has Java version compatibility issues, here's how to run the project using your IDE:

## IntelliJ IDEA (Recommended)

### **Step 1: Open Project**
1. File → Open → Select `C:\Users\DELL\Desktop\Aayush\spring-blog-api`
2. Open as Gradle project
3. Wait for Gradle sync to complete

### **Step 2: Configure Java SDK**
1. File → Project Structure → Project
2. Set Project SDK to Java 21 (or 17)
3. Set Project language level to match SDK

### **Step 3: Run Application**
1. Find `SpringBlogApiApplication.java`
2. Right-click → Run 'SpringBlogApiApplication'
3. Or click the green play button next to the main method

### **Alternative: Create Run Configuration**
1. Run → Edit Configurations
2. Add new Spring Boot configuration
3. Main class: `com.huseynovvusal.springblogapi.SpringBlogApiApplication`
4. Set working directory to project root
5. Click OK and run

## Eclipse STS

### **Step 1: Import Project**
1. File → Import → Existing Gradle Project
2. Select project directory
3. Finish

### **Step 2: Configure Java**
1. Right-click project → Properties → Java Build Path
2. Set JRE to Java 21 or 17

### **Step 3: Run Application**
1. Right-click `SpringBlogApiApplication.java`
2. Run As → Spring Boot App

## VS Code

### **Step 1: Install Extensions**
- Extension Pack for Java
- Spring Boot Extension Pack

### **Step 2: Open Project**
1. File → Open Folder
2. Select project directory

### **Step 3: Run Application**
1. Open `SpringBlogApiApplication.java`
2. Click the Run button above the main method
3. Or press F5

## 🚀 Quick Alternative: Spring Boot Maven

If you have Maven installed, you can convert to Maven:

```bash
# Install Maven if not present
# Then run directly
mvn spring-boot:run
```

## 🎯 Current Status

**Issue:** Gradle 8.11.1 doesn't support Java 25 (class file major version 69)
**Solution:** Use IDE-based running or install Java 21

**What's Working:**
- ✅ Authentication caching fix implemented
- ✅ Configuration files ready
- ✅ Database setup instructions provided
- ✅ Setup scripts created

**What Needs Your Action:**
- Install Java 21 OR use IDE to run
- Install PostgreSQL or use Docker
- Run the application

## 📊 Application Access

Once running:
- API: http://localhost:8082/api/v1/
- Swagger: http://localhost:8082/api/v1/swagger-ui/index.html