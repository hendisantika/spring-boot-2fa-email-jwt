#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print section header
print_header() {
  echo -e "\n${GREEN}=== $1 ===${NC}\n"
}

# Sign Up
print_header "Sign Up"
curl -X POST "${BASE_URL}/sign-up" \
  -H "Content-Type: application/json" \
  -d '{
    "emailId": "test@example.com",
    "password": "password123"
  }' \
  -v

# Login
print_header "Login"
curl -X POST "${BASE_URL}/login" \
  -H "Content-Type: application/json" \
  -d '{
    "emailId": "test@example.com",
    "password": "password123"
  }' \
  -v

# Verify OTP for Sign Up
print_header "Verify OTP for Sign Up"
curl -X POST "${BASE_URL}/verify-otp" \
  -H "Content-Type: application/json" \
  -d '{
    "emailId": "test@example.com",
    "oneTimePassword": 123456,
    "context": "SIGN_UP"
  }' \
  -v

# Store tokens from OTP verification response
# Uncomment and modify to use actual tokens
# ACCESS_TOKEN="your-access-token-here"
# REFRESH_TOKEN="your-refresh-token-here"

# Verify OTP for Login
print_header "Verify OTP for Login"
curl -X POST "${BASE_URL}/verify-otp" \
  -H "Content-Type: application/json" \
  -d '{
    "emailId": "test@example.com",
    "oneTimePassword": 123456,
    "context": "LOGIN"
  }' \
  -v

# Refresh Token
print_header "Refresh Token"
curl -X PUT "${BASE_URL}/refresh-token" \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "your-refresh-token-here"
  }' \
  -v

# Get Joke
print_header "Get Joke"
curl -X GET "${BASE_URL}/joke" \
  -H "Accept: application/json" \
  -v

# Get User Details
print_header "Get User Details"
curl -X GET "${BASE_URL}/user" \
  -H "Authorization: Bearer your-access-token-here" \
  -H "Accept: application/json" \
  -v

# Delete User Account
print_header "Delete User Account"
curl -X DELETE "${BASE_URL}/user" \
  -H "Authorization: Bearer your-access-token-here" \
  -H "Accept: application/json" \
  -v

# Verify OTP for Account Deletion
print_header "Verify OTP for Account Deletion"
curl -X POST "${BASE_URL}/verify-otp" \
  -H "Content-Type: application/json" \
  -d '{
    "emailId": "test@example.com",
    "oneTimePassword": 123456,
    "context": "ACCOUNT_DELETION"
  }' \
  -v

echo -e "\n${GREEN}All API tests completed${NC}"