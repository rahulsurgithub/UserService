# UserService
User service scaler back end project for masters program


The project is in working condition with unit tests in place 100% code coverage

Take main branch for latest code
https://github.com/rahulsurgithub/UserService/tree/main

# UserService

User service backend project (Spring Boot, Maven) used for a masters program.  
Project provides user and role management REST APIs with caching, tests and CI-ready configuration.

## Key points
1. Languages / frameworks
   1. Java (Spring Boot)
   2. SQL (RDBMS via JPA)
   3. Maven

2. Status
   1. Project is working and has unit tests with 100% code coverage (see tests folder / CI).

## Features
1. User CRUD (REST)
2. Role CRUD (REST)
3. Read-path caching (Caffeine in-process; optional ElastiCache Redis for multi-instance)
4. DTOs, validation, and repository layer (JPA)
5. DB indexing and query projections for optimized reads
6. CI/CD friendly (GitHub Actions / Docker-ready)

## Prerequisites (Windows)
1. JDK 17\+ installed and `JAVA_HOME` set
2. Maven or use provided wrapper:
   1. `mvnw.cmd` (Windows)
3. PostgreSQL or MySQL for local testing (or use Docker)
4. IntelliJ IDEA 2024.3.5 (project author environment)
5. Optional: Docker & Docker Desktop if using containers

## Quick start (Windows)
1. Clone:
   1. `git clone https://github.com/rahulsurgithub/UserService.git`
   2. `cd UserService`
2. Build:
   1. `.\mvnw.cmd -DskipTests clean package`
3. Run:
   1. Run from Maven: `.\mvnw.cmd spring-boot:run`
   2. Or run packaged jar: `java -jar target\userservice-<version>.jar`
4. IntelliJ:
   1. Open project, import Maven changes, create a Run configuration for `Application` (Spring Boot), or use Maven tool window -> spring-boot:run.

## Configuration
1. Primary config files:
   1. `src/main/resources/application.properties` (or `application.yml`)
2. Common properties to set:
   1. `spring.datasource.url=jdbc:mysql://localhost:3306/userservicedatabase`
   2. `spring.datasource.username=${DB_USER}`
   3. `spring.datasource.password=${DB_PASS}`
   4. `spring.redis.host=<redis-endpoint>`
   5. `spring.redis.port=6379`
3. Use Windows environment variables or IntelliJ Run configuration to inject secrets locally. In AWS use Secrets Manager / SSM.

## Caching
1. In-process: Caffeine cache configured in `CacheConfig` (example cache names: `users`, `roles`).
2. Annotated usage:
   1. `@Cacheable(value = "users", key = "'all'")` on `getAllUsers()`
   2. `@CacheEvict(value = "users", allEntries = true)` on mutating endpoints.
3. For multiple app instances use ElastiCache Redis and Spring Cache Redis adapter.
4. Example `getAllUsers()` behavior:
   1. Cold: first call queries DB and populates cache.
   2. Warm: subsequent calls served from memory (low latency).
   3. Mutations (create/update/delete) evict the cache to keep data consistent.

## API (high level)
1. Users
   1. `GET /users` — list users (`@Cacheable` on read)
   2. `GET /users/{id}` — get user details
   3. `POST /users` — create user (evicts users cache)
   4. `PUT /users/{id}` — update user (evicts users cache)
   5. `DELETE /users/{id}` — delete user (evicts users cache)
2. Roles
   1. `GET /roles` — list roles (`@Cacheable`)
   2. `GET /roles/{id}` — role details (`@Cacheable`)
   3. `POST /roles/create` — create role (`@CacheEvict`)
   4. `PUT /roles/{id}` — update role (`@CacheEvict`)
   5. `DELETE /roles/{id}` — delete role (`@CacheEvict`)

Example curl:
```bash
curl -X GET http://localhost:8080/users