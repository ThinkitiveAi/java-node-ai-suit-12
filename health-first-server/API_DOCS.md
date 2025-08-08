# HealthFirst Server API Documentation

## Table of Contents
1. [Authentication](#authentication)
2. [Patient Registration](#patient-registration)
3. [Email Verification](#email-verification)
4. [Error Handling](#error-handling)
5. [Validation Rules](#validation-rules)

## Authentication

### Login

**Endpoint**: `POST /api/v1/patient/auth/login`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Success Response (200 OK)**:
```json
{
  "token": "jwt-token-here",
  "type": "Bearer",
  "refreshToken": "refresh-token-here",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "phoneNumber": "+1234567890",
  "emailVerified": true,
  "phoneVerified": false,
  "tokenExpiry": "2025-08-01T20:00:00"
}
```

### Refresh Token

**Endpoint**: `POST /api/v1/patient/auth/refresh-token`

**Headers**:
- `Authorization: Bearer <refresh-token>`

**Success Response (200 OK)**:
Same as login response with new tokens.

## Patient Registration

### Register New Patient

**Endpoint**: `POST /api/v1/patient/register`

**Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "dateOfBirth": "1990-01-01",
  "gender": "MALE",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zip": "10001"
  },
  "emergencyContact": {
    "name": "Jane Doe",
    "phone": "+1987654321",
    "relationship": "Spouse"
  },
  "medicalHistory": ["Hypertension", "Asthma"],
  "insuranceInfo": {
    "provider": "Blue Cross",
    "policyNumber": "BC123456789"
  }
}
```

**Success Response (201 Created)**:
```json
{
  "success": true,
  "message": "Patient registered successfully. Verification email sent.",
  "data": {
    "patientId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "emailVerified": false,
    "phoneVerified": false,
    "registeredAt": "2025-08-01T15:30:45"
  }
}
```

## Email Verification

### Verify Email

**Endpoint**: `GET /api/v1/patient/verify-email?token=<verification-token>`

**Success Response (200 OK)**:
```json
{
  "success": true,
  "message": "Email verified successfully"
}
```

### Resend Verification Email

**Endpoint**: `POST /api/v1/patient/resend-verification?email=user@example.com`

**Success Response (200 OK)**:
```json
{
  "success": true,
  "message": "Verification email resent successfully"
}
```

## Error Handling

### Error Response Format
```json
{
  "success": false,
  "message": "Error message describing the issue",
  "errors": {
    "fieldName": ["Validation error message"],
    "anotherField": ["Another validation error"]
  }
}
```

### Common Error Status Codes
- `400 Bad Request`: Invalid request data or validation failed
- `401 Unauthorized`: Authentication failed or invalid token
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource already exists (e.g., duplicate email)
- `422 Unprocessable Entity`: Validation errors in request
- `500 Internal Server Error`: Server error

## Validation Rules

### Patient Registration
- **firstName**: Required, 2-50 characters, letters and spaces only
- **lastName**: Required, 2-50 characters, letters and spaces only
- **email**: Required, valid email format, must be unique
- **phoneNumber**: Required, valid international phone format, must be unique
- **password**: 
  - At least 8 characters
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one number
  - At least one special character
- **confirmPassword**: Must match password
- **dateOfBirth**: Required, must be a valid past date, user must be at least 13 years old
- **gender**: Required, must be one of: MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
- **address**:
  - street: Required, max 200 characters
  - city: Required, max 100 characters
  - state: Required, max 50 characters
  - zip: Required, valid postal code format
- **emergencyContact** (optional):
  - name: Max 100 characters
  - phone: Valid phone format
  - relationship: Max 50 characters
- **medicalHistory** (optional): Array of strings
- **insuranceInfo** (optional):
  - provider: Max 100 characters
  - policyNumber: Max 50 characters

### Authentication
- **email**: Required, valid email format
- **password**: Required, non-empty
