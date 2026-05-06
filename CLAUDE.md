# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**k2pbot** is a Generic Chatbot Platform built on Spring Boot 4.0.6 and Spring AI 2.0.0-M4. It provides a configurable framework for specialized chatbots with LLM model routing, RAG, memory strategies, tool integration, and runtime policy enforcement — all backed by a relational database.

## Build & Run Commands

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=PersistenceIntegrationTest

# Run a single test method
mvn test -Dtest=PersistenceIntegrationTest#testCreateAndRetrieveChatSession
```

Service starts on port 8080. Verify with:
- `GET /actuator/health` → `{"status":"UP"}`
- `GET /api/v1/ping`

## Architecture

### Package Structure (under `com.k2bot.ai.chatbot`)

The project is a **single Maven module** organized by layer/phase into packages:

| Package | Phase | Status | Purpose |
|---|---|---|---|
| `persistence` | 1 | ✅ Done | JPA entities, repositories, services, converters |
| `config` | 2 | ✅ Done | Configuration layer — loader, resolver, validator, cache, provider |
| `prompt` | 3 | ✅ Done | Prompt assembly — variable resolution, template rendering, instruction composition, validation |
| `modelrouting` | 4 | ✅ Done | LLM model routing — `HeuristicRequestClassifier`, `DefaultRoutingPolicyEvaluator`, `DefaultModelRoutingService`, `ChatOptionsFactory` |
| `memory` | 5 | ✅ Done | Memory strategy abstraction — `ChatMemory`, `ChatMemoryAdvisor`, `MemoryAdvisorFactory`, IN_MEMORY and JDBC strategies |
| `rag` | 6 | 🔲 Pending | RAG & knowledge base integration (stub) |
| `tools` | 7 | 🔲 Pending | Tool registry & execution (stub) |
| `audit` | 8 | 🔲 Pending | Execution audit & observability (stub) |
| `orchestration` | 9 | 🔲 Pending | Runtime orchestration (stub — `ChatRequest` added) |
| `web` | — | ✅ Done | REST controllers + global exception handler |
| `common` | — | ✅ Done | `ChatbotException` and shared utilities |

### Persistence Layer (Phase 1 — Complete)

15 JPA entities across the core domain:
- **Configuration entities:** `AssistantEntity`, `PromptTemplateEntity`, `ModelRouteEntity`, `MemoryPolicyEntity`, `RagPolicyEntity`, `ToolPolicyEntity`, `SafetyPolicyEntity`, `ResponsePolicyEntity`, `KnowledgeBaseEntity`, `TenantAssistantOverrideEntity`
- **Operational entities:** `ChatSessionEntity`, `ChatMessageEntity`, `ChatExecutionEntity`, `ToolExecutionAuditEntity`, `RagRetrievalAuditEntity`

All entities extend `BaseAuditEntity` which provides `created_at`, `updated_at`, `created_by`, `updated_by`, and `row_version` (optimistic locking via `@Version`).

Key persistence utilities:
- `JsonMapConverter` / `StringListJsonConverter` — persist `Map<String,Object>` and `List<String>` as JSON CLOBs
- `ConversationPersistenceService` — session/message CRUD
- `ExecutionAuditPersistenceService` — audit record persistence
- `SchemaStartupValidator` — validates schema on boot

### REST Conventions

- Base path: `/api/v1`
- All errors go through `GlobalExceptionHandler`, which returns a standardized payload with `timestamp`, `status`, `errorCode`, and `message`

## Database

**Production:** PostgreSQL at `localhost:5432/chatbot` (user: `chatbot`, password: `chatbot`)

**Test:** H2 in-memory with PostgreSQL compatibility mode — same Liquibase migrations run on startup.

Schema is managed exclusively by Liquibase (`ddl-auto=none`). Migrations live in:
```
src/main/resources/db/changelog/
  db.changelog-master.xml
  changes/0001_initial_schema.xml
```

## Key Conventions

- **No Lombok** — use standard Java getters/setters
- **Spring Boot patterns throughout** — no custom frameworks
- **Feature flags** in `application.properties` gate each phase (`app.feature.<phase>.enabled`); all are `false` until the phase is implemented
- **Each phase must keep the app buildable and startable** before merging
- **Multi-tenancy** is baked into `ChatSessionEntity` via `tenantId`; tenant-specific config overrides live in `TenantAssistantOverrideEntity`
- REST API uses `@Transactional` boundaries at the service layer, not the controller layer
- Test profile is `test` (activated via `@ActiveProfiles("test")` in test classes)

## Design Docs

The `docs/` folder contains the phased LLD and implementation plan. Read these before implementing any new phase:
- [docs/final-lld.md](docs/final-lld.md) — Authoritative low-level design
- [docs/generic_chatbot_implementation_plan.md](docs/generic_chatbot_implementation_plan.md) — Phase-by-phase implementation plan
