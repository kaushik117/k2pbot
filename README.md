# k2pbot — Generic Chatbot Platform

Single Maven Spring Boot project that hosts the generic chatbot platform.
See [docs/generic_chatbot_implementation_plan.md](docs/generic_chatbot_implementation_plan.md) for the phased delivery plan.

## Requirements

- Java 21 (JDK 21)
- Maven 3.9+

## Build

```bash
mvn clean install
```

## Run

```bash
mvn spring-boot:run
```

The service starts on port `8080` by default.

## Verify

- Health: `GET http://localhost:8080/actuator/health` returns `{"status":"UP"}`
- Ping:   `GET http://localhost:8080/api/v1/ping` returns service status, name and timestamp

## Project Layout

```
com.yourcompany.ai.chatbot
├── config         # Phase 2 — config layer
├── orchestration  # Phase 9 — runtime orchestration
├── prompt         # Phase 3 — prompt assembly
├── modelrouting   # Phase 4 — routing/chat options
├── memory         # Phase 5 — chat memory strategies
├── rag            # Phase 6 — retrieval-augmented generation
├── tools          # Phase 7 — tool registry & execution
├── persistence    # Phase 1 — JPA entities & repositories
├── audit          # Phase 8 — execution audit/observability
├── web            # REST controllers & exception advice
├── common         # shared exceptions & types
└── support        # cross-layer utilities
```

## Notes for Phase 0

- `DataSourceAutoConfiguration` and `HibernateJpaAutoConfiguration` are excluded from the main application class so the service boots cleanly without a live PostgreSQL instance. Phase 1 (Persistence Layer) re-enables them once entities and migrations are in place.
- Datasource properties in `application.properties` are placeholders for Phase 1.
