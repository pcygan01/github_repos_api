# GitHub API Integration

This is a Spring Boot application that interacts with the GitHub API to retrieve repository information for a given user. The application filters out forks and provides detailed information about repositories including branch names and their last commit SHA.

## Requirements

- Java 21
- Maven
- Internet connection to access GitHub API

## Running the Application

1. Clone the repository
2. Build the project using Maven:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   mvn spring-boot:run
   ```
   
The application will start on port 8080.

## API Endpoints

### 1. Get User Repositories

Retrieves a list of GitHub repositories for a specified user, excluding forks. For each repository, it also fetches branch information.

**Endpoint:** `GET /api/users/{username}/repositories`

**Path Parameters:**
- `username`: GitHub username

**Response:**
- Status Code: 200 OK
- Body: List of repositories with their branch information
  ```json
  [
    {
      "name": "repository-name",
      "owner": {
        "login": "username"
      },
      "branches": [
        {
          "name": "main",
          "sha": "commit-sha"
        }
      ]
    }
  ]
  ```

### 2. Error Responses

**User Not Found:**
- Status Code: 404 Not Found
- Body:
  ```json
  {
    "status": 404,
    "message": "User not found: username"
  }
  ```

## Testing

The application includes both unit and integration tests. To run the tests:

```
mvn test
```