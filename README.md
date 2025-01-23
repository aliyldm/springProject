# GPU Information System

This is a web application that provides detailed information about NVIDIA GPUs, allowing users to comment and share likes.

## Features

- User registration and login
- GPU list viewing
- GPU detail pages
- Commenting and liking GPUs
- Different user roles (Super Admin, Data Manager, Normal User)
- Secure authentication and authorization

## Technologies

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- Thymeleaf
- HTML/CSS
- Maven

## Installation

### Requirements

- Java 17 or higher
- MySQL 8.0
- Maven

### Database Setup

1. Create a new database in MySQL:
```sql
CREATE DATABASE buttondemo;
```

### Application Setup

1. Clone the project:
```bash
git clone https://github.com/aliyldm/springProject.git
cd springProject
```

2. Create `src/main/resources/application.properties` file and add the following configuration:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/buttondemo?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
```

3. Build and run the project:
```bash
mvn clean install
mvn spring-boot:run
```

4. Go to http://localhost:8080 in your browser

## User Roles

1. **Super Admin**
   - Username: `admin`
   - Password: `admin123`
   - Can perform all operations

2. **Data Manager**
   - Username: `manager`
   - Password: `manager123`
   - Can manage GPU data

3. **Normal User**
   - Created through registration
   - Can view GPUs, comment, and like

## API Endpoints

- `GET /api/gpu`: Lists all GPUs
- `GET /api/gpu/{id}`: Shows details of a specific GPU
- `POST /api/gpu/{id}/comment`: Adds a comment to a GPU
- `POST /api/gpu/{id}/like`: Likes/unlikes a GPU
- `POST /register`: New user registration
- `POST /login`: User login

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details. 