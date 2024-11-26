# Account Management System

---
## Table of Contents

1. [Technologies](#technologies)
2. [Getting Started](#getting-started)
3. [Event Sourcing](#event-sourcing)
---
## **Technologies**

This project uses the following technologies:

- Java JDK 12 or later
- IDE (e.g., IntelliJ IDEA, Eclipse)
- Docker
- MySql
- Spring boot
- H2 Database (for testing)


---

## Getting Started

### Running the Application Locally

To get the application up and running on your local machine, follow the steps below.

### 1. Build the Project

Before running the application, make sure everything is set up correctly by building the project. You can do this using the following command:

```bash
./gradlew clean build
```

This will clean any previous builds and create a fresh build of the project.

### 2. Set Up the Database

The application requires a database to be up and running. To set it up, we'll use Docker to create the necessary environment. You can set up the database with the following command:

```bash
docker compose up
```

This command will pull the necessary Docker images and start the database service defined in your `docker-compose.yml` file.

### 3. Tear Down the Database (Optional)

If you want to stop the database and remove the volumes (e.g., to reset the environment), you can tear down the database with the following command:

```bash
docker compose down -v
```

This will stop and remove all containers, networks, and volumes defined in the Docker Compose file.

### 4. Run the Application

Once the database is set up, you can start the application with the following command:

```bash
./gradlew bootRun
```

This will launch the application, and it should be accessible locally.

---

## **Event Sourcing**

This application uses the event sourcing pattern to maintain a consistent view of the state of a user's account.

### Why I'm Using Event Sourcing?
I'm using event sourcing for this service as I believe it's an interesting way to preserve history and build a consistent view of the state of an entity.
Since you store every event that happens, you get a full history of all changes which can be useful for tracking what happened in the past, auditing actions, or finding bugs.

If you need to know the state of something in the past, you can rebuild it by replaying the events. For example, if you need to know the balance of a bank account at a certain point in time, you can replay the transactions and fund events to get that balance.

``` 
This was something I was planning to implement provided I had more time
```

Event Sourcing allows you to change how things work without changing the way your system behaves. You can add new types of events or change the logic that handles them without changing the rest of the system.
This gives this service more flexibility to work in architectures that are more event driven.


