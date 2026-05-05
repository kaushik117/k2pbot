# Generic Chatbot Platform — Detailed Implementation Plan

## Objective

This document defines a phased implementation plan for the generic chatbot platform.  
Each major layer is treated as an implementation phase, and every phase is currently marked as **Pending**.

## Global Delivery Rules

These rules apply to **every phase**:

- phase status starts as **Pending**
- codebase remains a **single Maven project**
- each major layer is implemented in a **separate package**
- configuration uses **`application.properties`**, not YAML
- by the end of each phase:
  - `mvn clean install` must be successful
  - Spring Boot application must start successfully
  - basic health/startup validation must pass
- no phase should leave the repository in a broken state
- partial implementation is allowed only if the phase acceptance criteria are still met
- where later phases need placeholders, provide stub implementations that do not break startup

---

# Phase 0 — Project Scaffolding and Baseline Bootstrapping

**Status:** Completed

## Goal

Create the baseline Spring Boot application structure, Maven setup, package layout, shared dependencies, placeholder beans, and default configuration so that the project builds and the service starts successfully.

## Scope

- create single Maven project
- create Spring Boot main application
- define package structure
- add core dependencies
- add `application.properties`
- add actuator and health endpoint
- add base exception handling scaffold
- add placeholder config classes/interfaces for future phases
- add basic controller to verify service startup
- add base test structure

## Package Setup

```text
com.k2pbot.ai.chatbot
├── config
├── orchestration
├── prompt
├── modelrouting
├── memory
├── rag
├── tools
├── persistence
├── audit
├── web
├── common
└── support
```

## Tasks

1. Initialize Spring Boot project with:
   - spring-boot-starter-web
   - spring-boot-starter-actuator
   - spring-boot-starter-validation
   - spring-boot-starter-data-jpa
   - spring-boot-starter-test
   - spring-ai starter dependencies as per chosen provider strategy
   - database driver for selected RDBMS
   - lombok not allowed

2. Create main class:
   - `GenericChatbotApplication`

3. Create baseline package folders and placeholder package-info or marker classes where useful.

4. Add `application.properties` with:
   - server port
   - application name
   - actuator exposure
   - datasource placeholder values
   - JPA defaults
   - logging defaults
   - feature flags for incomplete layers

5. Add a simple startup verification endpoint:
   - `GET /api/v1/ping`

6. Add global exception advice scaffold.

7. Add basic health endpoint support through actuator.

8. Add Maven plugin configuration:
   - spring-boot-maven-plugin
   - surefire plugin
   - compiler plugin for Java 21

9. Add README/build instructions for local run.

## Suggested `application.properties`

```properties
spring.application.name=generic-chatbot-platform
server.port=8080

management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

spring.datasource.url=jdbc:postgresql://localhost:5432/chatbot
spring.datasource.username=chatbot
spring.datasource.password=chatbot
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

logging.level.root=INFO
logging.level.com.k2pbot.ai.chatbot=DEBUG

app.feature.config-layer.enabled=false
app.feature.orchestration.enabled=false
app.feature.prompt-layer.enabled=false
app.feature.model-routing.enabled=false
app.feature.memory.enabled=false
app.feature.rag.enabled=false
app.feature.tools.enabled=false
```

## Deliverables

- bootable Spring Boot application
- project structure committed
- `application.properties` created
- health and ping endpoint working
- baseline unit/integration test skeleton

## Acceptance Criteria

- `mvn clean install` passes
- application starts with `mvn spring-boot:run`
- `/actuator/health` returns UP
- `/api/v1/ping` returns success
- no missing bean errors at startup
- all future layer packages exist in project structure

## Test Cases

### Unit Tests
- application context loads
- ping controller returns expected response
- exception advice bean loads

### Integration Tests
- `GET /actuator/health` returns HTTP 200
- `GET /api/v1/ping` returns HTTP 200 and expected payload

### Manual Tests
- start application locally
- confirm logs show successful boot
- verify no datasource misconfiguration causes immediate startup failure if profile is configured correctly

---

# Phase 1 — Persistence Layer (Spring Data JPA Foundation)

**Status:** Completed

## Goal

Implement the relational persistence foundation using Spring Data JPA, including base entities, repositories, converters, and persistence services required by later phases.

## Scope

- base JPA configuration
- audit base entity
- JSON converters
- assistant config entities
- chat session and message entities
- execution audit entities
- repository interfaces
- persistence service layer
- DB migration scripts

## Tasks

1. Implement persistence package structure:
   - `entity`
   - `repository`
   - `converter`
   - `service`
   - `config`
   - `support`

2. Create `BaseAuditEntity`.

3. Implement JSON converters:
   - `JsonMapConverter`
   - `StringListJsonConverter`

4. Implement entities:
   - `AssistantEntity`
   - `PromptTemplateEntity`
   - `ModelRouteEntity`
   - `MemoryPolicyEntity`
   - `RagPolicyEntity`
   - `ToolPolicyEntity`
   - `SafetyPolicyEntity`
   - `ResponsePolicyEntity`
   - `KnowledgeBaseEntity`
   - `TenantAssistantOverrideEntity`
   - `ChatSessionEntity`
   - `ChatMessageEntity`
   - `ChatExecutionEntity`
   - `ToolExecutionAuditEntity`
   - `RagRetrievalAuditEntity`

5. Implement repositories.

6. Implement persistence services:
   - `ConversationPersistenceService`
   - `ExecutionAuditPersistenceService`

7. Add DB migration scripts using Flyway or Liquibase.
   - recommended: Flyway for simpler phase-wise SQL versioning

8. Add startup validation for schema initialization.

## Deliverables

- JPA entities
- repository interfaces
- persistence services
- migration scripts
- successful DB-backed application startup

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully with DB connection
- schema migration runs successfully on startup
- repositories are created without bean errors
- basic insert/select integration tests pass
- no entity mapping exceptions on startup

## Test Cases

### Unit Tests
- JSON converters serialize/deserialize correctly
- persistence service methods handle basic success scenarios

### Integration Tests
- application context loads with JPA
- schema migration runs successfully
- insert and fetch assistant entity
- insert and fetch chat session
- insert and fetch chat message ordered by creation
- insert and fetch execution audit

### Manual Tests
- start service against local DB
- inspect generated tables
- verify audit/base columns are populated
- verify schema version table is updated

---

# Phase 2 — Config Layer

**Status:** Completed

## Goal

Implement the configuration layer that loads, validates, resolves, caches, and serves runtime-ready assistant configuration.

## Scope

- config runtime models
- config loader
- config validator
- config resolver
- config cache
- config provider facade
- runtime override model
- config exceptions

## Tasks

1. Create config package structure:
   - `api`
   - `model`
   - `loader`
   - `resolver`
   - `validator`
   - `cache`
   - `service`
   - `exception`

2. Implement runtime models:
   - `ResolvedAssistantConfig`
   - prompt/routing/rag/memory/tool/safety/response resolved configs

3. Implement raw config bundle model.

4. Implement `AssistantConfigLoader` backed by JPA repositories.

5. Implement validation rules:
   - structural
   - referential
   - semantic
   - policy

6. Implement `AssistantConfigResolver`.

7. Implement cache:
   - recommended: Caffeine

8. Implement `AssistantConfigProvider`.

9. Add support for tenant-specific overrides.

10. Add support for restricted runtime overrides.

## Deliverables

- config provider API
- DB-backed config loading
- resolved config objects
- validation framework
- working config cache

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- config provider returns resolved config for seeded assistant
- invalid config is rejected with expected exception
- cache hit/miss behavior works
- tenant override merge works correctly

## Test Cases

### Unit Tests
- config resolver merges defaults + tenant overrides + runtime overrides correctly
- validator rejects invalid configurations
- cache stores and returns resolved config
- runtime override restrictions are enforced

### Integration Tests
- config provider loads seeded assistant config from DB
- invalid DB config causes validation failure
- cache-enabled retrieval returns consistent config
- tenant-specific override changes effective config

### Manual Tests
- seed one assistant config into DB
- start service
- call internal debug or admin endpoint to inspect resolved config
- modify config and verify cache eviction/refresh path if implemented

---

# Phase 3 — Prompt Assembly Layer

**Status:** Completed

## Goal

Implement prompt assembly from templates, runtime variables, guardrails, tool instructions, and response policy.

## Scope

- prompt runtime models
- prompt variable resolver
- template renderer
- instruction composer
- prompt validator
- prompt assembly facade

## Tasks

1. Create prompt package structure.

2. Implement models:
   - `PromptAssemblyInput`
   - `PromptAssemblyResult`
   - `PromptRenderMetadata`

3. Implement `PromptVariableResolver`.

4. Implement template renderer using Spring AI `PromptTemplate`.

5. Implement instruction composers:
   - system instructions
   - developer instructions
   - grounded-answer instructions
   - tool instructions
   - response-format instructions

6. Implement `PromptValidator`.

7. Implement `PromptAssemblyService`.

8. Add safeguards for prompt size and required sections.

## Deliverables

- prompt assembly service
- rendered system/developer/user prompts
- prompt validation and metadata

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- prompt assembly works for seeded assistant config
- variable substitution works
- grounded mode instructions appear when required
- prompt validation rejects malformed prompt output

## Test Cases

### Unit Tests
- variable resolver returns expected merged variable map
- template renderer substitutes placeholders correctly
- instruction composer injects expected rules
- validator rejects blank system prompt
- validator rejects missing grounded instructions when grounded mode enabled

### Integration Tests
- prompt assembly service produces final prompt for seeded config
- prompt version is propagated correctly
- tool instruction injection happens only when tools enabled

### Manual Tests
- inspect prompt assembly output through debug logging or internal debug endpoint
- verify prompt changes when response policy changes

---

# Phase 4 — Model Routing Layer

**Status:** Pending

## Goal

Implement policy-driven model routing and chat options selection.

## Scope

- routing input/output models
- request classifier
- routing evaluator
- model selection validator
- fallback policy
- chat options factory

## Tasks

1. Create model routing package structure.

2. Implement models:
   - `RoutingInput`
   - `RequestClassification`
   - `ModelSelectionResult`
   - `FallbackSelection`

3. Implement heuristic classifier.

4. Implement ordered routing policy evaluator.

5. Implement model selection validator.

6. Implement `ChatOptionsFactory`.

7. Add support for:
   - default model
   - route priority
   - fallback model
   - streaming flag
   - max token / temperature selection

8. Add model registry validation hooks.

## Deliverables

- routing service
- classifier
- fallback handling
- chat options creation

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- routing service selects expected model for seeded rules
- fallback model is resolved where configured
- unsupported streaming/model combinations are rejected
- route priority order is honored

## Test Cases

### Unit Tests
- simple request maps to default/simple model
- long prompt maps to long-context route
- tool-heavy request maps to tool-capable route
- fallback is returned when configured
- invalid selected model is rejected

### Integration Tests
- routing service uses DB-loaded routing policy
- chat options factory produces options from selected route
- model hint is restricted according to policy

### Manual Tests
- test multiple seeded routes
- inspect selected model through response metadata or debug logs

---

# Phase 5 — Memory Layer

**Status:** Pending

## Goal

Implement configurable chat memory strategy with support for disabled, in-memory, and DB-backed modes.

## Scope

- memory package
- memory strategy interfaces
- memory advisor factory
- memory store selection
- session-memory integration
- optional history window abstraction

## Tasks

1. Create memory package structure.

2. Implement memory strategy abstraction.

3. Implement supported memory modes:
   - NONE
   - IN_MEMORY
   - JDBC/DB-backed policy mapping

4. Implement `MemoryAdvisorFactory`.

5. Integrate session-based memory lookup.

6. Add policy mapping from resolved memory config to runtime memory behavior.

7. Keep chat history separate from memory abstraction.

## Deliverables

- memory strategy interfaces
- memory advisor factory
- working memory integration for runtime

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- stateless mode works
- in-memory mode works
- DB-backed mode wiring works if selected
- memory does not break startup when disabled

## Test Cases

### Unit Tests
- memory strategy factory returns correct implementation by config
- disabled memory returns no advisors
- in-memory mode returns memory advisor
- invalid memory policy is rejected

### Integration Tests
- conversation with memory enabled includes prior messages
- conversation with memory disabled behaves statelessly
- DB-backed strategy wiring loads without bean issues

### Manual Tests
- run two consecutive chat requests in same session
- verify memory-enabled path includes prior context
- verify memory-disabled path does not

---

# Phase 6 — RAG Layer

**Status:** Pending

## Goal

Implement runtime-selectable RAG integration using knowledge base metadata from config and retriever/advisor assembly from the RAG layer.

## Scope

- knowledge base registry
- retriever resolution
- RAG advisor factory
- grounded mode support
- RAG audit integration
- metadata filter support

## Tasks

1. Create rag package structure.

2. Implement knowledge base registry abstraction.

3. Implement retriever resolution from `knowledgeBaseId`.

4. Implement `RagAdvisorFactory`.

5. Support:
   - topK
   - threshold
   - metadata filters
   - grounded-answer-required mode

6. Integrate retrieval audit persistence.

7. Add failure handling for KB missing / retrieval unavailable.

## Deliverables

- RAG advisor factory
- knowledge base resolution
- retriever mapping
- grounded answer support

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- RAG-enabled assistant resolves KB correctly
- grounded mode fails safely if KB unavailable
- retrieval audit is persisted
- RAG-disabled assistant runs without retrieval

## Test Cases

### Unit Tests
- KB registry resolves correct retriever by knowledge base id
- grounded mode validator rejects missing KB
- RAG advisor factory returns advisor only when enabled

### Integration Tests
- seeded KB metadata resolves during runtime
- RAG-enabled request executes through retrieval path
- RAG audit record is written
- missing KB produces expected controlled failure

### Manual Tests
- run RAG-enabled assistant
- verify citations/metadata appear when configured
- verify insufficient-context behavior in grounded mode

---

# Phase 7 — Tools Layer

**Status:** Pending

## Goal

Implement configurable tool registry, tool selection, and tool execution integration with assistant-level allowlists.

## Scope

- tool registry
- tool selection service
- local tool integration
- optional MCP / REST tool abstraction
- tool policy enforcement
- tool execution audit

## Tasks

1. Create tools package structure.

2. Implement `ToolRegistry`.

3. Implement `ToolSelectionService`.

4. Support tool types:
   - local Spring bean tool
   - REST-backed tool abstraction
   - future MCP-backed tool abstraction

5. Enforce tool allowlist from resolved config.

6. Integrate tool execution audit persistence.

7. Add approval/timeout policy hooks.

## Deliverables

- tool registry
- tool selection service
- policy-based tool exposure
- tool audit integration

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- only allowed tools are exposed for assistant
- disallowed tool selection is rejected
- tool execution results are auditable
- service starts even when no tools are enabled

## Test Cases

### Unit Tests
- tool registry resolves configured tool
- tool selection returns assistant-approved subset
- disallowed tool name is rejected
- timeout policy is mapped correctly

### Integration Tests
- assistant with enabled local tool can execute tool path
- assistant without tools does not expose tool execution
- tool audit entry is written after execution

### Manual Tests
- trigger a request that causes a tool call
- verify selected tool is executed
- verify disallowed tool request is blocked

---

# Phase 8 — Audit and Observability Layer

**Status:** Pending

## Goal

Implement execution audit services and operational observability hooks across runtime execution.

## Scope

- execution audit service
- audit persistence integration
- request start/success/failure capture
- tool audit integration
- RAG audit integration
- logging and correlation ids

## Tasks

1. Create audit package structure.

2. Implement `ExecutionAuditService`.

3. Persist:
   - request start
   - request completion
   - request failure
   - selected model
   - enabled tools
   - KB id
   - token usage if available
   - latency

4. Add correlation/request id propagation.

5. Add structured logging support.

6. Add metrics counters/timers if using Micrometer.

## Deliverables

- runtime audit service
- structured audit persistence
- request correlation support

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- success audit is persisted for successful request
- failure audit is persisted for failed request
- request id is traceable through logs
- audit does not block runtime startup

## Test Cases

### Unit Tests
- audit service builds expected audit records
- success and failure mapping works
- missing optional values handled safely

### Integration Tests
- successful chat request writes execution audit
- tool call writes tool audit
- RAG-enabled request writes retrieval audit
- failure path writes failure audit

### Manual Tests
- inspect DB records after successful/failed request
- verify request id in logs matches DB audit record

---

# Phase 9 — Orchestration Layer Integration

**Status:** Pending

## Goal

Implement the runtime orchestration layer that ties together config, routing, prompt assembly, memory, RAG, tools, execution, and audit.

## Scope

- conversation runtime service
- streaming runtime service
- execution context factory
- execution plan factory
- executor implementations
- advisor chain factory
- chat client registry/model registry

## Tasks

1. Implement orchestration package structure.

2. Implement:
   - `ConversationRuntimeService`
   - `StreamingConversationRuntimeService`
   - `ExecutionContextFactory`
   - `ExecutionPlanFactory`
   - `ChatExecutionExecutor`
   - `StreamingChatExecutionExecutor`

3. Implement advisor chain assembly.

4. Implement chat client/model registry.

5. Integrate:
   - config provider
   - routing service
   - prompt service
   - memory advisor factory
   - RAG advisor factory
   - tool selection
   - audit service
   - persistence services

6. Implement failure handling and fallback path.

## Deliverables

- end-to-end runtime execution path
- sync and streaming orchestration
- integrated execution plan

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- end-to-end synchronous request works
- streaming endpoint works if enabled
- fallback path works when primary model fails
- runtime persists session/history/audit successfully

## Test Cases

### Unit Tests
- execution context factory builds complete context
- execution plan factory includes expected advisors/tools
- executor calls client registry correctly
- fallback path is triggered on primary failure

### Integration Tests
- synchronous chat request completes end-to-end
- streaming request produces chunks end-to-end
- session/history persistence works after response
- audit records are written after execution

### Manual Tests
- send real request through REST endpoint
- verify response, persisted messages, audit, and startup logs
- simulate failure and verify fallback

---

# Phase 10 — API Layer and External Contract Finalization

**Status:** Pending

## Goal

Implement external REST and streaming APIs, DTO mapping, validation, and error handling.

## Scope

- request/response DTOs
- controllers
- DTO mappers
- exception advice
- validation annotations
- session/history endpoints
- admin/debug endpoints as needed

## Tasks

1. Implement request DTOs.
2. Implement response DTOs.
3. Implement streaming DTOs.
4. Implement controllers:
   - chat
   - streaming chat
   - session
   - optional admin/debug
5. Implement DTO mappers.
6. Implement exception advice.
7. Add request validation and custom validators.

## Deliverables

- external REST contracts
- streaming contract
- error response schema
- validated request handling

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully
- API endpoints return expected schema
- validation errors return standardized error payload
- session endpoints return persisted history
- admin/debug endpoints work if enabled

## Test Cases

### Unit Tests
- DTO validation behaves correctly
- mapper converts internal objects to API response
- exception advice maps exceptions to expected response

### Integration Tests
- POST `/api/v1/chat` returns expected contract
- POST `/api/v1/chat/stream` returns stream/event output
- GET session endpoints return expected data
- invalid request returns validation error schema

### Manual Tests
- test all main REST endpoints with Postman/curl
- validate response payload shape
- validate error payload shape

---

# Phase 11 — Hardening, Non-Functional Checks, and Release Readiness

**Status:** Pending

## Goal

Stabilize the platform for release through non-functional validation, cleanup, and operational readiness checks.

## Scope

- startup hardening
- configuration cleanup
- profile separation
- documentation
- performance sanity checks
- security review hooks
- package dependency review
- final build validation

## Tasks

1. Review package dependency directions.
2. Remove temporary stubs and dead code.
3. Split configuration by profile in `application.properties` strategy if needed.
4. Validate startup under local/dev/test profiles.
5. Add readiness checklist.
6. Add operational runbook sections.
7. Add basic load/performance sanity tests.
8. Review logging for sensitive data masking.
9. Verify no circular bean dependencies.

## Deliverables

- cleaned codebase
- final properties setup
- release checklist
- deployment readiness notes

## Acceptance Criteria

- `mvn clean install` passes
- application starts successfully in target profiles
- no circular dependency or bean creation issues
- no sensitive data leaked in standard logs
- core APIs work after cleanup/hardening
- release checklist completed

## Test Cases

### Unit Tests
- final smoke coverage remains green
- no placeholder stubs left where core behavior required

### Integration Tests
- full application context loads in all supported profiles
- end-to-end happy path passes
- end-to-end failure/fallback path passes

### Manual Tests
- run complete smoke suite
- verify production-like properties load correctly
- verify startup, health, and main runtime flows

---

# Suggested Delivery Order Summary

| Phase | Name | Status |
|---|---|---|
| 0 | Project Scaffolding and Baseline Bootstrapping | Completed |
| 1 | Persistence Layer (Spring Data JPA Foundation) | Completed |
| 2 | Config Layer | Completed |
| 3 | Prompt Assembly Layer | Completed |
| 4 | Model Routing Layer | Pending |
| 5 | Memory Layer | Pending |
| 6 | RAG Layer | Pending |
| 7 | Tools Layer | Pending |
| 8 | Audit and Observability Layer | Pending |
| 9 | Orchestration Layer Integration | Pending |
| 10 | API Layer and External Contract Finalization | Pending |
| 11 | Hardening, Non-Functional Checks, and Release Readiness | Pending |

---

# Final Execution Rule

At the end of **every phase**:

- status remains tracked
- code is committed in a buildable state
- `mvn clean install` must pass
- Spring Boot application must start successfully
- at least phase-level smoke tests must pass

