# Skill Management Service

This is the skill management service which does the following :-
- exposes `People` and `Skills` as resources over REST API
- allows create/read/update/delete operations on both `People` and `Skills`
- allows management of skills of the people via the APIs

## Technology stack

The following technologies have been used in this project :-

- Framework - Spring Boot
- Reducing boilerplate - Lombok
- Data access - Spring Data JPA
- API documentation - Swagger

## Build

To build the application, run the following command from the root directory :-

```
mvn clean install
```

This will run unit tests and build the application.

## Profiles to run the application

The following Spring profiles have been provided in the application to run it against different environments :-

- docker - To run in a docker container
- local - To run locally

## How to run

### Run in docker

- Run the following commands from the application root directory to bring up the docker container :-

```
docker build -t nhsbsa/sts:latest .

mv .env.example .env

docker-compose up -d
```

### Run locally

- Install and run Postgres on port 5432
- Run the service either from IntelliJ/Eclipse or terminal and pass the environment variable `-D spring.profiles.active=local`

## How to test

Once the application is running :- 

- Go to `http://localhost:8080/actuator/health` to see the health of the application

- Go to `http://localhost:8080/swagger-ui.html` to see the API contract. All the operations can be performed using this

- Check the data in the database using any tool (e.g. - Postico)

## How the application works

- `Person` and `Skill` are the 2 main entities having many-many relationship
- They are linked by the `Person_Skill` bridge join table which holds the skill level for a person having a particular skill
- Skills can be created/read/updated/deleted using the Skills API. The Skills API does not expose the person-skill relationship and therefore these relations can't be managed using the Skills API
- People can be created/read/updated/deleted using the People API. This also exposes the person-skill relationships and they can be managed using this API
- When a person is created (POST /people) with a skill, the following happens :-
	- If the skill already exists, only the person is created and an association is created between the person and the existing skill
	- If the skill does not exist already, the skill is created first, then the person created and finally the association created between the person and the skill
- When a person is updated (PUT /people) with a skill, the following happens :-
	- If the skill already exists, only the person details are updated and the association between the person and the existing skill is updated (if skill level changes)
	- If the skill does not exist already, the skill is created first, then the person details updated and finally the association created between the person and the skill
	- If an existing skill is not sent in the request, that skill is deleted from the association table

## Technical debt

- Use a data mapping library (e.g. mapstruct)
- Integration tests
- Docker image versioning
