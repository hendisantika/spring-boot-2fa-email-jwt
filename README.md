# Spring Boot 2FA Email JWT

A Spring Boot application implementing Email-based Two-Factor Authentication (2FA) with JWT (JSON Web Token) for secure
user authentication.

## Features

- User registration and account management
- Two-factor authentication using email OTP (One-Time Password)
- JWT-based authentication with access and refresh tokens
- Secure API endpoints with Spring Security
- OpenAPI/Swagger documentation
- H2 in-memory database for data storage

## Technologies Used

- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- Spring Mail for email services
- JWT (JSON Web Token) for authentication
- Google Guava for caching
- H2 Database
- Lombok
- OpenAPI/Swagger for API documentation

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Email account for sending OTP (configured for Yahoo Mail by default)

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/spring-boot-2fa-email-jwt.git
   cd spring-boot-2fa-email-jwt
   ```

2. Configure email settings in `application.properties`:
   ```properties
   spring.mail.host=your-smtp-server
   spring.mail.username=your-email
   spring.mail.password=your-password
   spring.mail.port=587
   spring.mail.properties.mail.smtp.starttls.enable=true
   spring.mail.properties.mail.smtp.auth=true
   ```

3. Build the application:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the application at `http://localhost:8080`
6. Access the Swagger UI at `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Authentication

- **POST /sign-up** - Create a new user account
    - Request body: `SignupRequestDto` (email and password)
    - Response: OTP sent to email for verification

- **POST /login** - Authenticate user credentials
    - Request body: `LoginRequestDto` (email and password)
    - Response: OTP sent to email for verification

- **POST /verify-otp** - Verify OTP and get JWT tokens
    - Request body: `OtpVerificationRequestDto` (email, OTP, and context)
    - Response: `LoginSuccessDto` (access token and refresh token)

- **PUT /refresh-token** - Refresh access token
    - Request body: `TokenRefreshRequestDto` (refresh token)
    - Response: New access token

### User Management

- **DELETE /user** - Request account deletion (requires authentication)
    - Response: OTP sent to email for verification

- **GET /user** - Get user details (requires authentication)
    - Response: User information

## Authentication Flow

1. **Registration**:
    - User submits email and password
    - System creates an account and sends OTP to the email
    - User verifies OTP with context `SIGN_UP`
    - System marks email as verified and issues JWT tokens

2. **Login**:
    - User submits email and password
    - System validates credentials and sends OTP to the email
    - User verifies OTP with context `LOGIN`
    - System issues JWT tokens

3. **Account Deletion**:
    - Authenticated user requests account deletion
    - System sends OTP to the email
    - User verifies OTP with context `ACCOUNT_DELETION`
    - System marks the account as inactive

## Configuration

### JWT Configuration

```properties
id.my.hendisantika.twofaemailjwt.config.jwt.secret-key=your-secret-key
```

### OTP Configuration

```properties
id.my.hendisantika.twofaemailjwt.otp.expiration-minutes=5
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

Hendi Santika - [hendisantika](https://s.id/hendisantika)
