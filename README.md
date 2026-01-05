# Task Management App
Task Management App Backend is a Spring Boot application that provides a REST API for managing projects, tasks, and users.
It supports JWT authentication, role-based access, and allows users to create, update, and track tasks efficiently.

## Table of Contents
- [Key Features](#key-features)
- [Running the backend locally](#running-the-backend-locally)
- [Swagger documentation](#swagger-documentation)
- [API Endpoint Details](#api-endpoint-details)
- [Postman](#postman)

## Key Features
- JWT-based authentication and authorization
- User registration and profile management
- Project management: create, update, delete projects
- Task management: create, update, delete tasks
- Commenting for tasks
- Role-based access for admin and standard users
- Swagger documentation for easy API testing

## Running the backend locally
1. Before running the backend, make sure the following tools are installed:
### Java 17 or higher
Check if installed:
```bash
java -version
```
Expected:
```bash
openjdk version "17.x.x"
```
If Java is missing or the version is lower than 17, download it:
https://www.oracle.com/java/technologies/downloads/

### Maven
Maven is used to build and run the application.

Check if installed:
```bash
mvn -version
```

Expected:
```bash
Apache Maven 3.x.x
```

If Maven is missing, install it:
https://maven.apache.org/download.cgi

### Git

Used to clone the project.

Check if installed:
```bash
git --version
```

Expected:
```bash
git version 2.x.x
```

If Git is not installed:
https://git-scm.com/


2. Clone the repository:
   ```bash
   git clone https://github.com/mategames-team/backend.git
   cd backend
   git checkout dev
   ```

3. Build and run the project using Maven:
    ```bash
   mvn spring-boot:run
    ```

## Swagger documentation
To test endpoints use Swagger documentation by link (when running the app locally, instructions below):
http://localhost:8080/api/swagger-ui/index.html
### How to Use the API Documentation (Swagger UI)
Important! To run manual update of the database using AdminGameController endpoints, you need to log in as ADMIN.
This API uses JWT authentication.
To access protected endpoints, you must register, log in, and authorise Swagger with your token.

1️⃣ Register a New User
- Open Swagger UI
- Find the Authentication section
- Click POST /auth/register
- Click “Try it out”
- Fill in the request body
- Click Execute
  ✅ If registration is successful, the user is created.

2️⃣ Log In and Get JWT Token
- In the Authentication section
- Open POST /auth/login
- Click “Try it out”
- Enter your credentials
- Click Execute
  📌 The response will contain a JWT token, for example:
  {
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  👉 Copy this token

3️⃣ Authorise Swagger with JWT Token
- At the top-right corner of Swagger UI, click the 🔒 Authorize button
- In the popup window:
- Paste your token in this format:
- Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  ⚠️ Important: The word Bearer and a space must be included
- Click Authorize
- Click Close
  ✅ Swagger is now authorised

4️⃣ Call Protected Endpoints
Once authorised, you can access endpoints that require authentication.
📌 Swagger will now automatically attach the JWT token to each authorised request.

5️⃣ Logging Out / Token Expiry
If you refresh the page or your token expires:
- Click 🔒 Authorize again
- Paste a new token

## API Endpoint Details.
The backend API is organised into several controllers, each responsible for a specific domain of the application. 
separation follows REST and clean architecture principles, making the system easier to understand, maintain, and extend.

#### AuthenticationController
Handles user authentication and security.
This controller is the entry point for accessing protected endpoints across the application.

#### UserController
Manages user-related operations.
All endpoints in this controller require authentication.

#### ProjectController
Manages projects that group tasks together.

#### TaskController
Handles all task-related operations.
Tasks represent individual units of work and are always associated with a project.

#### LabelController
Manages labels used to categorise and filter tasks.
Labels allow flexible organisation and filtering of tasks.

#### CommentController
Handles comments associated with tasks.
Comments are used for collaboration and discussion around tasks.

#### AttachmentController
Manages file attachments linked to tasks.
Attachments allow users to add documents, images, or other files relevant to a task.

## Postman
A Postman collection is available to simplify testing the API.
👉 [Open Online Bookstore Postman Collection](https://web.postman.co/workspace/49ed7a22-2d52-45ef-8ca1-c68f46105379/collection/40367151-d08c65ae-a3f2-4e1c-8e3b-2f0151cfe0e6?action=share&source=copy-link&creator=40367151)

How to use this Postman collection:
1. Click the link above.
2. Import the collection into your Postman app.
3. Run requests against http://localhost:8080/api/
4. Authenticate by registering and/or logging in to get a JWT token.
5. Go to the Authorization tab.
6. Choose Bearer Token as the Auth Type.
7. Paste the JWT token you received into the token field to access all protected endpoints.
