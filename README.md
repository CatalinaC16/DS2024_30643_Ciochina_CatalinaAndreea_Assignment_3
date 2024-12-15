# Energy Monitoring Application

This repository contains an energy monitoring application consisting of three microservices: `usersms` ,`devicesms`,`measurems`
and a frontend application in Angular (`frontend_energyapp`). Each microservice has its own PostgreSQL database.

## Prerequisites

- Docker and Docker Compose installed.
- Node.js and npm (only required for local development of the Angular frontend).
- Java 17 (only required for local development of the microservices).

## Project Structure

- **UserMicroService**: Contains code for `usersms` microservice.
- **DeviceMicroService**: Contains code for `devicesms` microservice.
- **MCMicroService**: Contains code for `measurems` microservice.
- **Frontend_EnergyApp**: Angular-based frontend application.
- **docker-compose.yml**: Docker Compose configuration for deploying the entire application stack.

For generating the jar of each microservice run before step 1. 
#mvn clean install
For generating the dist folder on frontend run this command before step 1.
#ng build --prod


## Build and Run with Docker

### Step 1: Build and Start Containers

To build and run all services using Docker Compose, run the following command in a cmd
from the project root directory:

#docker-compose up --build

This command will:

Build Docker images for usersms, devicesms, and the Angular frontend.
Set up PostgreSQL databases for each microservice.
Start all containers.

### Step 2: Access the Application
Frontend: The frontend will be accessible at http://frontend.localhost
Users Microservice: Accessible at http://user.localhost
Devices Microservice: Accessible at http://device.localhost
Monitoring Microservice: Accessible at http://measure.localhost

### Step 3: Starting the Simulator application
The simulator is responsible with reading the values from a .csv file and transmiting them over the queue
to the monitoring microservice.

The device.id for the simulator can be set in the config.properties file (UUID type)
You can start the app by running one of the .jar files after setting the device id in the config file.
The runnig of the .jar file can be done with:
 
	java -jar SMDSimulator-0.0.1-SNAPSHOT_86.jar

### Step 4: Stopping the Application
To stop and remove containers, use in a cmd from root directory:

#docker-compose down

This command stops all running containers and removes them along with any networks created by Docker Compose.

And for the Simulator a Ctrl+C is enough to stop the Spring application.