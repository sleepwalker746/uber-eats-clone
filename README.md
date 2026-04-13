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
