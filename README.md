# Mocard Server

A Spring Boot-based server application for AI-powered card generation and management system with chat functionality.

## Features

- 🤖 **AI Chat Integration**: OpenAI GPT integration for intelligent conversations
- 🎨 **Card Generation**: AI-powered card creation and management
- 👤 **User Management**: Complete user authentication and authorization system
- 💳 **Payment Processing**: Apple In-App Purchase integration
- 🔒 **Security**: JWT-based authentication with Redis session management
- 📱 **WebSocket Support**: Real-time communication capabilities
- 🐰 **Message Queue**: RabbitMQ integration for asynchronous processing
- 📊 **Database**: MySQL with JPA/Hibernate ORM
- 🚀 **Caching**: Redis and Caffeine multi-level caching
- 📝 **Logging**: Comprehensive logging with P6Spy SQL monitoring

## Tech Stack

- **Framework**: Spring Boot 2.7.0
- **Java Version**: Java 8
- **Database**: MySQL 5.x
- **Cache**: Redis + Caffeine
- **Message Queue**: RabbitMQ
- **ORM**: Spring Data JPA + Hibernate
- **Authentication**: JWT (JSON Web Tokens)
- **AI Integration**: OpenAI GPT API
- **Build Tool**: Maven
- **Connection Pool**: Druid

## Project Structure

```
src/main/java/com/boringland/mocardserver/
├── annotation/          # Custom annotations for security and validation
├── aspect/             # AOP aspects for cross-cutting concerns
├── auth/               # Authentication and authorization components
├── config/             # Configuration classes
├── constant/           # Application constants
├── controller/         # REST API controllers
├── entity/             # Data entities and DTOs
│   ├── dto/           # Data Transfer Objects
│   ├── model/         # JPA entity models
│   ├── request/       # Request objects
│   └── response/      # Response objects
├── exception/          # Custom exception classes
├── handler/           # Exception handlers
├── interceptor/       # HTTP interceptors
├── listener/          # Event listeners for WebSocket/SSE
├── service/           # Business logic services
└── util/              # Utility classes
```

## Prerequisites

- Java 8 or higher
- Maven 3.6+
- MySQL 5.x or higher
- Redis 6.x or higher
- RabbitMQ 3.8+

## Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd mocard_server
   ```

2. **Configure the database**
   - Create a MySQL database
   - Import the SQL scripts from the `sql/` directory:
     ```bash
     mysql -u username -p database_name < sql/user_account.sql
     mysql -u username -p database_name < sql/card_info.sql
     mysql -u username -p database_name < sql/card_context.sql
     ```

3. **Configure application properties**
   - Copy the example configuration files:
     ```bash
     cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
     cp src/main/resources/application-prod.yml.example src/main/resources/application-prod.yml
     ```
   - Update the configuration files with your actual values

4. **Build the project**
   ```bash
   mvn clean compile
   ```

5. **Run the application**
   ```bash
   # Development mode
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   
   # Or build and run JAR
   mvn clean package
   java -jar target/mocard-server-dev.jar
   ```

## Configuration

The application supports multiple environments through Spring profiles:

- `dev` - Development environment (default)
- `test` - Testing environment  
- `prod` - Production environment

### Required Configuration

Update the following properties in your configuration files:

#### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:p6spy:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
```

#### Redis Configuration
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

#### RabbitMQ Configuration
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: your_username
    password: your_password
```

#### OpenAI Configuration
```yaml
chatgpt:
  apiKey: your_openai_api_key
  apiHost: https://api.openai.com/
```

#### JWT Configuration
```yaml
jwt:
  secret: your_jwt_secret_key
  expirt: 86400
  authorization: access-token
```

#### Apple In-App Purchase Configuration
```yaml
apple:
  verifyReceipt:
    testUrl: https://sandbox.itunes.apple.com/verifyReceipt
    url: https://buy.itunes.apple.com/verifyReceipt
    password: your_apple_shared_secret
```

## API Endpoints

### Authentication
- `POST /api/user/login` - User login
- `POST /api/user/register` - User registration

### Chat
- `POST /api/chat/send` - Send chat message
- `GET /api/chat/history` - Get chat history

### User Management
- `GET /api/user/profile` - Get user profile
- `PUT /api/user/profile` - Update user profile

### Purchase
- `POST /api/purchase/verify` - Verify Apple In-App Purchase

### Settings
- `GET /api/settings` - Get application settings
- `PUT /api/settings` - Update settings

## Development

### Running Tests
```bash
mvn test
```

### Code Style
The project follows standard Java coding conventions. Make sure to:
- Use proper indentation (4 spaces)
- Follow camelCase naming conventions
- Add appropriate JavaDoc comments
- Keep methods focused and concise

### Database Migrations
The application uses Hibernate's `ddl-auto: update` for automatic schema updates in development. For production, consider using proper database migration tools.

## Deployment

### Building for Production
```bash
mvn clean package -Pprod
```

### Docker Deployment (Optional)
Create a `Dockerfile` in the project root:
```dockerfile
FROM openjdk:8-jre-slim
COPY target/mocard-server-prod.jar app.jar
EXPOSE 8010
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
For production deployment, you can override configuration using environment variables:
- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL=jdbc:mysql://...`
- `SPRING_DATASOURCE_USERNAME=...`
- `SPRING_DATASOURCE_PASSWORD=...`

## Monitoring and Logging

- Application logs are configured via `logback-spring.xml`
- SQL queries are logged using P6Spy
- Default log location: `logs/` directory

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have questions, please:
1. Check the existing issues in the repository
2. Create a new issue with detailed information about the problem
3. Include relevant logs and configuration details (without sensitive information)

## Acknowledgments

- Spring Boot team for the excellent framework
- OpenAI for the GPT API
- All contributors who have helped improve this project
