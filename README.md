# Tic Tac Toe – Spring Boot REST API

> ⚠️ **Project Status: Inactive** – This application is no longer live and its deployment has been pulled.

A configurable Tic-Tac-Toe backend written with **Spring Boot 3**.  
It exposes a small REST API that the companion frontend (hosted at <https://tictactoe-frontend-delta.vercel.app/>) consumes.

---

## Features

* Create a new game with a custom board size.
* Make moves and get the updated board state & winner information.
* In-memory **H2** database for local development, pluggable SQL support for production.
* Ready for one-click deploy to Heroku (includes `Procfile`).

---

## Technology Stack

| Layer            | Tech                                                  |
|------------------|-------------------------------------------------------|
| Language         | Java 17                                              |
| Framework        | Spring Boot 3 (Web, Validation, Data JPA)            |
| Build Tool       | Maven 3 – wrapper included (`mvnw` / `mvnw.cmd`)      |
| Database (local) | H2 (in-memory, auto-creates on start-up)             |

---

## Running Locally

```bash
# Linux / macOS
a./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

By default the API will be available at <http://localhost:8080>.

### Useful Endpoints

| Method | Endpoint              | Description                     |
|--------|-----------------------|---------------------------------|
| POST   | `/game`               | Create a new game               |
| POST   | `/game/{id}/move`     | Make a move on an existing game |
| GET    | `/h2-console`         | H2 DB web console (dev only)    |

Example – **Create Game**
```http
POST /game HTTP/1.1
Content-Type: application/json

{
  "boardSize": 3,
  "firstPlayerSymbol": "X"
}
```

Response
```json
{
  "timestamp": "2025-07-05T05:34:21.123Z",
  "data": {
    "id": "4ecac9c9-1b7c-4201-8a11-0ac42a2fb12a",
    "boardSize": 3,
    "board": [
      ["", "", ""],
      ["", "", ""],
      ["", "", ""]
    ],
    "winner": null
  },
  "message": "GAME_CREATED"
}
```

Example – **Make Move**
```http
POST /game/4ecac9c9-1b7c-4201-8a11-0ac42a2fb12a/move
Content-Type: application/json

{
  "row": 0,
  "col": 2,
  "symbol": "O",
  "overwrite": false
}
```

---

## Configuration

All properties are externalised so the same jar can run anywhere.  
When **no** environment variables are supplied, Spring falls back to sensible development defaults.

| Property                 | Purpose                                   |
|--------------------------|-------------------------------------------|
| `PORT`                   | Port the server listens on (default 8080) |
| `JDBC_DATABASE_URL`      | JDBC URL to your database                 |
| `JDBC_DRIVER`            | JDBC driver class name                    |
| `DB_USERNAME`            | Database username                         |
| `DB_PASSWORD`            | Database password                         |
| `JPA_DIALECT`            | Hibernate dialect (e.g. `org.hibernate.dialect.PostgreSQLDialect`) |

Create a file `src/main/resources/application-local.properties` or set the variables in your shell / hosting provider.

---

## Building & Testing

```bash
# Compile & run unit tests
./mvnw clean verify

# Build executable jar (output in `target/`)
./mvnw clean package
```

---

## License

This repository is released under the MIT License – see `LICENSE` for details.
