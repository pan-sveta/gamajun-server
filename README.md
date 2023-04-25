# Gamajun Client Application

![Logo](src/main/resources/logo.png)

The Gamajun Server Application is a part of the Gamajun BPMN testing system.

The Gamajun System is specifically designed for educators who are teaching BPMN modeling.
This application aims to facilitate an interactive learning experience by offering a comprehensive system to train and verify the knowledge of BPMN diagrams.
Instructors can use this tool to create engaging lessons that reinforce students' understanding of business process modeling and notations, while students can benefit from a hands-on approach to mastering the intricacies of BPMN diagrams.

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Environment Variables](#environment-variables)

## Requirements
- Java 17+
- Maven

## Installation
### Local Installation
1. Clone the repository ```git clone git@github.com:pan-sveta/gamajun-client.git```
2. Configure environment variables (see [Environment Variables](#environment-variables))
3. Open in your favorite IDE and run the application

### Docker Installation
#### Local
1. Clone the repository ```git clone git@github.com:pan-sveta/gamajun-client.git```
2. Configure environment variables (see [Environment Variables](#environment-variables))
3. Build the image ```docker build -t gamajun-server .```
4. Run the container ```docker run -p 8080:8080 gamajun-server```

#### Use package
1. Pull the image ```docker pull ghcr.io/pan-sveta/gamajun-server:master```
2. Run the container ```docker run -p 8080:8080 {ENVIRONMENT CONFIGURATION} ghcr.io/pan-sveta/gamajun-server:master```

## Environment Variables
The following environment variables are required to run the application:

- POSTGRES_HOST - the host of the PostgreSQL database
- POSTGRES_USER - the username of the PostgreSQL database
- POSTGRES_PASSWORD - the password of the PostgreSQL database
- ADMIN_CODE - the code to create an admin account
- OAUTH2_CLIENT_ID - the client id of the OAuth2 server
- OAUTH2_CLIENT_SECRET - the client secret of the OAuth2 server
- CLIENT_URL - the url of the client application

Just copy the ```.env.example``` file and rename it to ```.env```. Then, fill in the required values.
