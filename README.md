# Web-Quiz-Engine
A small web engine for creating and passing quizzes via REST API. Based on Java and Spring Boot.

# Features
The following are the APIs to work with:
| Function  | Method | API |
| ------------- | ------------- | ------------- |
| Get all the quizzes  | GET  | 	/api/quizzes|
| Get a specific quiz with ID | GET | 	/api/quizzes/{id} |
| Create a quiz | POST  | 	/api/quizzes |
| Solve a quiz | POST | /api/quizzes/{id}/solve |
| Delete a quiz  | DELETE  | /api/quizzes/{id} |
| Register a new user | POST | /api/register |

# Technologies and concepts used
- Java 
- Spring Boot
- Spring Security for user authorization
- Spring Data JPA with H2 Database to store users, quizzes, and results
- Gradle for dependency management
