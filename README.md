Recipe Management System
A Spring Boot backend application for managing recipes and ingredients with user authentication.
Features

REST API - CRUD operations for recipes and ingredients
User Authentication - Google OAuth2 login integration
Data Security - Users can only access their own data
Testing - Unit and integration tests included
Database Integration - JPA/Hibernate with H2 database

Technologies Used
Backend:

Spring Boot 3.x, Spring Security, Spring Data JPA
H2 Database with JPA/Hibernate
Google OAuth2 authentication
Maven for build management

Development Tools:

Visual Studio Code with Java extensions
CURL for API testing
JUnit 5 and Mockito for testing

Testing & Quality:

Unit tests for service layer
Integration tests for controllers
Repository layer testing

Architecture
├── Controllers (REST endpoints)
├── Services (Business logic)
├── Repositories (Data access)
├── Models (JPA entities)
└── Security (OAuth2 config)
API Endpoints
Ingredients:

GET /ingredients - List user's ingredients
POST /ingredients - Create new ingredient
PUT /ingredients/{id} - Update ingredient
DELETE /ingredients/{id} - Delete ingredient

Recipes:

GET /recipes - List all recipes
POST /recipes - Create new recipe
PUT /recipes/{id} - Update recipe
DELETE /recipes/{id} - Delete recipe

Quick Start

Configure Google OAuth2 credentials in application.properties
Run: mvn spring-boot:run
Access: http://localhost:8080

Testing
bashmvn test  # Run all tests
Project Structure

Clean separation of concerns with MVC pattern
Repository pattern for data access
Service layer for business logic
Comprehensive test coverage
Security configuration for OAuth2
