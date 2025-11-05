# 🔗 Short Link Service 

A high-performance Spring Boot URL shortener with JWT authentication, Redis caching, and automated link expiration. Designed to showcase Java/Spring Boot best practices in building scalable web services.

## 📋 Prerequisites  
- Java 21  
- Docker (for Redis, PostgreSQL, and Spring Boot containers)  
- Maven  

## ✨ Key Features  
- **URL Shortening**: Convert long URLs into compact, shareable links  
- **Smart Redirection**: High-performance cached redirects using Redis  
- **Secure Authentication**: JWT-based access control  
- **Automatic Cleanup**: Scheduled removal of expired links  
- **Production-Ready**: Dockerized with PostgreSQL and Redis  

## ⚒️ Tech Stack  
- **Backend**: Java 21, Spring Boot 3.5.4  
- **Security**: JWT, Spring Security  
- **Database**: PostgreSQL (persistent storage)  
- **Caching**: Redis (performance optimization)  
- **Scheduling**: Spring Scheduler (automatic expiration handling)  
- **Containerization**: Docker (easy deployment)  

## ✈ Quick Start (Docker Setup)  

### 1. Clone the Repository  
```bash
git clone https://github.com/MateusdiSousa/ShortLink.git
cd ShortLink
```

### 2 Build the Project
```bash
mvn clean package -DskipTests
```

### 3 Lauch with Docker
```bash
docker-compose build
docker-compose up
```

## API Documentation
* Note: Most endpoints require JWT authentication (obtained via login)

### Authentication

#### 👤 Register New User
📝 Method: POST
🔗 Endpoint: /api/auth/register
📝 Request:
```json
{
    "email": "user@example.com",
    "password": "securePassword123"
}
```

#### User Login
📝 Method: POST
🔗 Endpoint: /auth/login
```json
{
    "email": "user@example.com",
    "password": "securePassword123"
}
```
Response: Returns JWT token for authenticated requests

### Link Management
#### Create Short Link
📝 Method: POST
🔗 Endpoint: /link
📌  Headers: Authorization: Bearer <JWT_TOKEN>
📝 Request:
```
{
    "link": "https://www.string.com",
    "shortLink": "string"
}
```

### Redirect to Original URL
📝 Method: GET
🔗 Endpoint: /link/{shortCode}
(No authentication required)

### User Operations
#### Get User's Links
📝 Method: GET
📌  Headers: Authorization: Bearer <JWT_TOKEN>
Response:
```
[
    {
        "id": "abc123",
        "originalLink": "https://original.com",
        "shortLink": "xyz789",
        "userEmail": "user@example.com"
    }, ...
]
```

## Accessing Swagger UI
For interactive API documentation and testing, visit:
http://localhost:3000/swagger-ui/index.html

## Development
* The API runs on port 3000 by default

* Swagger UI provides full endpoint documentation

* Docker environment includes:
    *   Spring Boot application
    * PostgreSQL database
    * Redis cache
