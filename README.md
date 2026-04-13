# 🍔 Uber Eats Clone - Microservices Architecture

A full-featured food delivery service clone built on a modern microservices architecture using Java, Spring Boot, Docker, and message brokers.

## 🏗 Architecture

The project is divided into independent microservices that communicate with each other via REST APIs and asynchronous messaging (RabbitMQ). Request routing is handled by an API Gateway, while service discovery is managed via Netflix Eureka.



### 📦 Microservices:
* **Eureka Server (`:8761`)**: Service Registry. Discovers and registers all running microservices in the network.
* **Gateway Service (`:8080`)**: The single entry point. Handles load balancing and routes incoming requests to the appropriate internal services.
* **Auth Service (`:8081`)**: Handles user registration, authentication, and JWT token issuance. Manages user accounts and roles. *(PostgreSQL)*
* **Restaurant Service (`:8082`)**: Manages restaurant information, menus, and item categories. *(PostgreSQL)*
* **Order Service (`:8083`)**: Processes user orders, manages carts, and tracks order statuses. *(PostgreSQL)*
* **Delivery Service (`:8084`)**: Assigns couriers and tracks real-time delivery progress. *(MongoDB)*
* **Notification Service (`:8085`)**: Asynchronously listens to RabbitMQ queues to send real-time notifications (e.g., Email/Push) to users regarding order updates and payment statuses.
* **Payment Service (`:8086`)**: Processes financial transactions and payment statuses. *(PostgreSQL)*


## 🛠 Tech Stack

* **Backend:** Java 21, Spring Boot 3.3.5, Spring Cloud (Gateway, Netflix Eureka)
* **Databases:** PostgreSQL 16 (Relational data), MongoDB (NoSQL for delivery tracking)
* **Message Broker:** RabbitMQ
* **Security:** Spring Security, JWT (JSON Web Tokens)
* **Mapping & Utils:** MapStruct, Lombok
* **DevOps:** Docker, Docker Compose, Gradle

---

## 🚀 How to Run Locally (via Docker)

Follow these steps to successfully build and run the entire infrastructure and microservices on your local machine.

### Prerequisites
Make sure you have the following installed:
* **Java 21** (JDK)
* **Docker** and **Docker Compose**
* **`.env` file**: Create a `.env` file in the root directory (next to `docker-compose.yml`) with the following variables:
  ```env
  DB_USER=postgres
  DB_PASSWORD=your_secure_password
  MONGODB_USER=admin
  MONGODB_PASSWORD=your_mongo_password
  JWT_SECRET_KEY=your_super_secret_jwt_key_here

### Step 1: Build the Executables (JARs)
Before handing the services over to Docker, you need to compile them. Make sure you open your terminal in the **root directory** of the project (where the `gradlew` file is located). 

This command will automatically traverse all your microservice modules (including the newly added `notification-service`) and build their `.jar` files.

**For Windows:**
```cmd
gradlew clean build -x test
```
**For Mac/Linux**
```bash
./gradlew clean build -x test
```

### Step 2: Run Docker Compose
Start the infrastructure (Postgres, Mongo, RabbitMQ) and all Spring Boot microservices:

```bash
# Stop and remove any old containers and volumes
docker-compose down -v

# Rebuild the Docker images using the fresh JAR files
docker-compose build --no-cache

# Run everything in the background
docker-compose up -d
```

### Step 3: Verify the Deployment
Wait for about 1-2 minutes for the databases to initialize and the `eureka-server` to start.
Open your browser and navigate to: **`http://localhost:8761`**

You should see the following instances successfully registered and showing a status of `UP` in Eureka:
* `GATEWAY-SERVICE`
* `AUTH-SERVICE`
* `RESTAURANT-SERVICE`
* `ORDER-SERVICE`
* `DELIVERY-SERVICE`
* `NOTIFICATION-SERVICE`
* `PAYMENT-SERVICE`

---

## 📝 Developer Notes & Troubleshooting

1. **Database Initialization:** The databases for the microservices are automatically created upon the first launch of the `postgres` container using the `init-databases.sql` script. Ensure this file is correctly mounted in the `docker-compose.yml`.
2. **Database Migrations (Flyway):** Database schema versioning and creation are strictly managed by **Flyway**. 
   * Migration scripts are located in `src/main/resources/db/migration`.
   * Flyway is configured with `baseline-on-migrate: true` to seamlessly integrate with environments.
   * **Hibernate is set to `ddl-auto: validate`**. This ensures that Hibernate makes no unauthorized changes to the database structure. Flyway creates the tables, and Hibernate only validates that the database schema perfectly matches the Java `@Entity` mappings.
3. **RabbitMQ Connection:** The `notification-service` and other event-driven services rely on RabbitMQ. Ensure the `rabbitmq` container is fully started before testing message-driven flows. Docker Compose handles this automatically via the `depends_on` condition.

## 📝 License
This project is open-source and available under the **Apache License 2.0**.

---
*Developed by [Arsenii Sidorovych](https://github.com/sleepwalker746)*

