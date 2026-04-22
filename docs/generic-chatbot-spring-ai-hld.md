# High Level Design: Generic Configurable Chatbot Platform using Spring AI

## 1. Purpose and Scope

This document defines the high-level design for a **generic chatbot platform** built using **Java 21**, **Maven 3**, and **Spring AI**. The platform provides a reusable conversational runtime that can be specialized at runtime using:

- assistant-specific prompt configuration
- runtime-selected RAG knowledge base
- configurable toolsets
- configurable model-routing rules
- configurable memory strategy

The goal is to avoid building separate chatbot applications for each business use case. Instead, a single generic platform will serve multiple specialized assistants through configuration and runtime policies.

This HLD covers:

- architectural goals
- system context
- high-level component decomposition
- runtime execution flow
- configuration model
- RAG strategy
- memory strategy
- model selection strategy
- tool orchestration
- data model
- key APIs
- observability and governance
- non-functional considerations

This HLD does not cover:

- low-level class implementation details
- exact prompt text
- exact vector chunking and ingestion algorithms
- UI implementation

---

## 2. Objectives

The platform must support the following objectives:

1. Provide a **single generic chatbot runtime**.
2. Allow **runtime specialization** using assistant configuration and knowledge-base selection.
3. Support **configurable prompts**, **configurable tools**, and **configurable model routing**.
4. Support **configurable memory** using either:
   - in-memory session storage
   - persistent database-backed storage
5. Support **RAG-based grounding** using one or more configured knowledge bases.
6. Support **dynamic model selection** based on prompt complexity and execution policy.
7. Ensure **tenant isolation**, **security**, **auditability**, and **observability**.
8. Keep the design extensible for future support of:
   - MCP-based tools
   - hybrid retrieval and reranking
   - structured output workflows
   - evaluation and guardrails

---

## 3. Requirements Summary

### 3.1 Functional Requirements

The platform shall:

- expose chat APIs for conversational interactions
- support assistant specialization via configuration
- load prompts dynamically at runtime
- attach tools dynamically at runtime
- attach RAG knowledge bases dynamically at runtime
- select models dynamically based on routing rules
- support memory policies configured per assistant
- support stateless and stateful chat sessions
- support synchronous and streaming responses
- support full audit trail of chat execution
- support retrieval citations where required
- support fallback behavior for model/tool/retrieval failures

### 3.2 Non-Functional Requirements

The platform shall:

- be modular and reusable
- be configurable without code changes for most assistant onboarding
- be secure and tenant-aware
- provide low-latency execution for simple use cases
- support horizontal scaling
- support observability for model, tool, and retrieval activity
- support future multi-provider AI model integration
- support future MCP-based enterprise integration

---

## 4. Assumptions

The following assumptions are used in this design:

- One generic platform will serve multiple assistants.
- Assistant behavior is driven primarily by configuration and runtime policies.
- RAG knowledge base can be selected using assistant defaults and optionally overridden at runtime.
- Tools are centrally registered and selectively enabled per assistant.
- Model selection is policy-based and controlled by the server.
- Chat memory and chat history are separate concerns.
- The platform will initially run as a Spring Boot application.
- Persistent configuration and history will be stored in a database.
- Spring AI features such as `ChatClient`, Advisors, tool calling, chat memory, and vector-store integrations are available.

---

## 5. Architectural Principles

The architecture follows these principles:

### 5.1 Generic Runtime, Config-Driven Specialization

Business specialization must happen through configuration rather than creating separate chatbot services.

### 5.2 Separation of Concerns

The platform separates:
- conversation orchestration
- prompt assembly
- model routing
- RAG retrieval
- tool execution
- memory handling
- audit and observability

### 5.3 Policy-Based Runtime Assembly

Each request is executed by dynamically assembling:
- selected model
- prompt layers
- advisor chain
- tool allowlist
- memory strategy
- retrieval strategy

### 5.4 Tenant-Aware Isolation

Knowledge bases, memory, and configuration must support tenant scoping.

### 5.5 Extensibility

The platform must support future adoption of:
- additional model providers
- MCP servers
- new vector stores
- evaluation pipelines
- guardrails and policy engines

---

## 6. System Context and External Integrations

The chatbot platform interacts with the following external actors and systems:

### 6.1 External Actors

- Web client
- Mobile client
- Internal business applications
- Admin/configuration users
- Optional enterprise chat channels

### 6.2 External Systems

- LLM providers
- Embedding model providers
- Vector store / knowledge base store
- Relational database for config/history/audit
- Optional document store for flexible payload retention
- Internal business APIs exposed as tools
- Optional MCP servers for enterprise tools/resources

### 6.3 System Context Diagram

```text
+----------------------+         +--------------------------------------+
| Client Channels      |         | Admin / Config Management             |
| Web / Mobile / API   |         | Assistant / Prompt / Policy Setup     |
+----------+-----------+         +------------------+-------------------+
           |                                        |
           |                                        |
           v                                        v
+-----------------------------------------------------------------------+
|                Generic Chatbot Platform (Spring AI)                   |
|                                                                       |
| Conversation API | Runtime Orchestrator | Model Routing | RAG | Tools |
| Memory | Audit | Observability | Policy Enforcement                  |
+----+-----------------+----------------+----------------+--------------+
     |                 |                |                |
     v                 v                v                v
+----------+     +-----------+   +-------------+   +-------------------+
| LLM APIs  |     | Vector DB |   | RDBMS       |   | Internal Tools /  |
| Providers |     | / KB      |   | Config/Audit|   | MCP Servers       |
+----------+     +-----------+   +-------------+   +-------------------+
```

---

## 7. Architecture Overview

The platform consists of a **single conversational runtime** that executes requests using runtime-selected policies. The assistant specialization is not implemented through separate codebases. Instead, each assistant is represented by configuration that defines:

- system/developer prompts
- enabled tools
- RAG defaults
- memory policy
- response policy
- model-routing policy

At request time, the runtime:

1. resolves assistant configuration
2. resolves execution context
3. classifies request complexity
4. selects the model
5. assembles advisors and tools
6. retrieves RAG context if enabled
7. executes the model using Spring AI `ChatClient`
8. stores memory/history/audit
9. returns the response

This design enables:
- reuse
- onboarding speed
- consistent governance
- reduced code duplication
- provider flexibility

---

## 8. High-Level Component Decomposition

### 8.1 Components

1. **Conversation API Layer**
2. **Assistant Configuration Service**
3. **Runtime Orchestrator**
4. **Prompt Assembly Service**
5. **Model Routing Service**
6. **RAG Service**
7. **Tool Registry and Tool Execution Layer**
8. **Memory Strategy Layer**
9. **Chat History and Audit Layer**
10. **Observability and Governance Layer**

### 8.2 Component Diagram

```text
+-----------------------------------------------------------------------+
|                         Conversation API Layer                        |
|  REST / Streaming endpoints                                           |
+-----------------------------------+-----------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                     Runtime Orchestrator                              |
| - load assistant config                                               |
| - resolve tenant/session/user context                                 |
| - apply runtime overrides                                             |
| - invoke routing, RAG, tools, memory                                  |
+------------+---------------+---------------+--------------------------+
             |               |               |
             v               v               v
+------------------+ +----------------+ +------------------------------+
| Prompt Assembly  | | Model Routing  | | Advisor Assembly             |
| - prompt layers  | | - complexity   | | - memory advisor            |
| - variables      | | - policy eval  | | - RAG advisor               |
| - response rules | | - model select | | - audit/policy advisors     |
+------------------+ +----------------+ +------------------------------+
             |               |               |
             +---------------+---------------+
                             |
                             v
+-----------------------------------------------------------------------+
|                Spring AI ChatClient Execution Layer                   |
| - selected ChatModel                                                  |
| - selected tools                                                      |
| - selected advisors                                                   |
| - sync/stream execution                                               |
+-------------+----------------+----------------+-----------------------+
              |                |                |
              v                v                v
+-------------------+ +------------------+ +---------------------------+
| Memory Layer      | | RAG Layer        | | Tool Layer                |
| in-memory / DB    | | KB Registry      | | Local tools / MCP tools   |
| windowed memory   | | retriever        | | allowlist, timeout policy |
+-------------------+ +------------------+ +---------------------------+
              |
              v
+-----------------------------------------------------------------------+
|                History / Audit / Metrics / Tracing                    |
+-----------------------------------------------------------------------+
```

---

## 9. Detailed Component Responsibilities

### 9.1 Conversation API Layer

Responsibilities:
- expose synchronous chat endpoint
- expose streaming endpoint
- expose session/history retrieval endpoints
- validate request payloads
- pass request to runtime orchestrator

Key characteristics:
- stateless service boundary
- security enforced before runtime invocation
- channel metadata captured for audit

### 9.2 Assistant Configuration Service

Responsibilities:
- load assistant configuration by assistant code and tenant scope
- resolve prompt templates
- resolve policy versions
- resolve default RAG, model, and memory settings
- validate active configuration

This service is the core specialization layer.

### 9.3 Runtime Orchestrator

Responsibilities:
- construct execution context
- merge assistant defaults with allowed runtime overrides
- invoke complexity classification
- coordinate model routing
- coordinate prompt assembly
- assemble tools and advisors
- invoke chat execution
- handle fallbacks
- persist execution results

This is the main brain of the platform.

### 9.4 Prompt Assembly Service

Responsibilities:
- build layered prompts:
  - platform prompt
  - assistant prompt
  - runtime context prompt
  - user prompt
- inject variables
- apply formatting instructions
- add citation rules when needed

### 9.5 Model Routing Service

Responsibilities:
- classify request complexity
- map request type to model policy
- select provider/model/parameters
- enforce routing constraints
- apply fallback chain when needed

### 9.6 RAG Service

Responsibilities:
- resolve knowledge base configuration
- resolve retriever/vector store
- execute retrieval
- enforce metadata filters
- pass retrieved context into advisor/prompt chain
- optionally record citation metadata

### 9.7 Tool Registry and Execution Layer

Responsibilities:
- maintain central registry of available tools
- resolve allowed tools for assistant/request
- attach tool callbacks
- enforce timeout/authorization/logging policy
- support future MCP-based tool resolution

### 9.8 Memory Strategy Layer

Responsibilities:
- determine whether memory is enabled
- choose memory mode:
  - none
  - in-memory
  - DB-backed
- manage model context window memory
- keep memory separate from history

### 9.9 Chat History and Audit Layer

Responsibilities:
- store full conversation history
- store execution metadata
- store tool invocation audit
- store retrieval audit
- store model selection audit

### 9.10 Observability and Governance Layer

Responsibilities:
- collect metrics
- emit tracing spans
- capture token usage
- capture latency by model/tool/retrieval
- enforce policy controls
- support production monitoring and compliance

---

## 10. Runtime Specialization Model

### 10.1 Specialization Inputs

The generic runtime is specialized using:

- `assistantCode`
- assistant configuration
- tenant context
- runtime overrides
- selected knowledge base
- selected memory policy
- selected model policy
- selected tools

### 10.2 Runtime Override Policy

Allowed runtime overrides may include:
- knowledge base identifier
- memory mode
- subset of allowed tools
- response mode options
- optional model hint

Disallowed runtime overrides:
- unrestricted model forcing
- unrestricted tool injection
- unrestricted prompt replacement
- cross-tenant knowledge base access

---

## 11. Spring AI Design Mapping

The platform maps its runtime responsibilities to Spring AI features as follows:

| Platform Concern | Spring AI Capability |
|---|---|
| chat invocation | `ChatClient` |
| model integration | `ChatModel` |
| cross-cutting orchestration | Advisors |
| chat memory | `ChatMemory` and repository abstractions |
| tool calling | tool callback support |
| RAG | advisor-based retrieval augmentation |
| streaming | `ChatClient` streaming support |
| observability | Spring AI observability support |

### 11.1 Design Choice

`ChatClient` is used as the execution boundary.  
Advisors are used as the main extension mechanism for:
- memory
- RAG
- audit
- policy checks
- response shaping

---

## 12. Model Routing Strategy

### 12.1 Objective

Model selection must be dynamic and policy-driven. The model used for a request must depend on request complexity and execution requirements.

### 12.2 Routing Inputs

The following inputs are used for model routing:

- prompt length / estimated input tokens
- presence of RAG
- need for tool execution
- structured output requirement
- expected reasoning complexity
- latency target
- cost sensitivity
- assistant-level allowed models

### 12.3 Routing Approach

The recommended routing approach is:

#### Stage 1: Heuristic Classification
Fast deterministic rules evaluate:
- prompt size
- tool usage need
- RAG requirement
- response structure requirement
- conversation depth

#### Stage 2: Optional Lightweight Classification
If needed, a smaller model or rule classifier may assign a request category such as:
- SIMPLE
- KNOWLEDGE_QA
- TOOL_HEAVY
- LONG_CONTEXT
- ANALYTICAL
- STRUCTURED_OUTPUT

#### Stage 3: Policy Mapping
The category maps to:
- provider
- model
- temperature
- max output tokens
- streaming preference
- fallback model

### 12.4 Routing Principles

- Server remains the final authority for model selection.
- Frontend may provide hints but cannot override governance policy.
- High-risk assistants may use stricter routing rules.
- Fallback models are configured explicitly.

---

## 13. Prompt Architecture

### 13.1 Prompt Layers

Prompt construction uses layered composition:

1. **Platform Prompt**
   - non-negotiable global policies
   - security rules
   - response safety rules
   - tool usage constraints

2. **Assistant Prompt**
   - assistant role
   - domain behavior
   - tone/style
   - specialization instructions

3. **Runtime Context Prompt**
   - tenant/channel/user context
   - locale
   - session metadata
   - execution mode

4. **User Prompt**
   - user message

### 13.2 Prompt Storage

Prompt templates should be externalized and versioned. They should not be hardcoded in business logic.

Storage options:
- relational tables
- versioned configuration files
- admin-managed configuration service

### 13.3 Prompt Governance

Prompt changes should support:
- versioning
- activation/deactivation
- audit of changes
- rollback to previous version

---

## 14. RAG Architecture

### 14.1 Objective

Provide grounded responses using runtime-selected knowledge bases.

### 14.2 RAG Design

The design includes:
- Knowledge Base Registry
- Retriever resolution by `knowledgeBaseId`
- metadata filters
- configurable `topK`
- similarity threshold
- optional citation support

### 14.3 Knowledge Base Registry

The KB registry maps a logical knowledge base identifier to:
- vector store connection
- embedding model
- retriever strategy
- metadata filters
- tenant scope
- operational status

### 14.4 Retrieval Flow

1. resolve KB configuration
2. build retriever
3. execute search
4. filter and rank results
5. inject retrieved context via advisor
6. optionally persist citation/retrieval metadata

### 14.5 RAG Modes

Assistants may support one of these modes:
- `DISABLED`
- `OPTIONAL`
- `REQUIRED_GROUNDED`

If mode is `REQUIRED_GROUNDED`, low-confidence retrieval must result in a controlled response instead of free-form unsupported answering.

### 14.6 Future Enhancements

Future versions may add:
- hybrid retrieval
- reranking
- query rewriting
- answer groundedness checks
- source quality scoring

---

## 15. Memory Architecture

### 15.1 Objective

Support configurable conversation memory using either in-memory or persistent storage.

### 15.2 Memory vs History

This design explicitly separates:

- **Chat Memory**: compressed or windowed context supplied to the model
- **Chat History**: full durable application history for audit and user experience

### 15.3 Supported Memory Modes

1. `NONE`
   - no memory
   - each request is independent unless history is explicitly reloaded

2. `IN_MEMORY_WINDOW`
   - windowed memory in application memory
   - useful for ephemeral sessions and low-cost deployments

3. `DB_WINDOW`
   - durable repository-backed memory
   - suitable for enterprise production use

### 15.4 Memory Configuration Parameters

- enabled flag
- store type
- message window size
- session TTL
- persist history flag
- summarization enabled flag (future)

### 15.5 Design Principles

- memory repository does not replace chat history store
- memory should be tenant-scoped and session-scoped
- memory failure policy is configurable:
  - fail
  - degrade to stateless mode

---

## 16. Tool Architecture

### 16.1 Objective

Allow the generic chatbot to use configurable tools safely and selectively.

### 16.2 Tool Types

The design supports:
- local Spring bean tools
- internal API tools
- future MCP server tools

### 16.3 Tool Registry

A central Tool Registry maintains:
- tool name
- tool type
- availability
- timeout policy
- authorization requirements
- response size constraints

### 16.4 Tool Resolution Flow

1. assistant config defines allowed tools
2. runtime override may request subset of allowed tools
3. tool registry resolves executable tool callbacks
4. execution layer applies audit/timeout/policy checks
5. tool results are returned to the model

### 16.5 Tool Governance Rules

- default deny; explicit allowlist
- per-assistant tool mapping
- per-tool timeout
- audit each invocation
- support future role-based tool controls

---

## 17. Data Model and Configuration Model

### 17.1 Logical Data Entities

#### Assistant Configuration
- assistant
- prompt versions
- model routing policy
- memory policy
- RAG policy
- tool policy
- response policy

#### Runtime Data
- chat session
- chat message history
- chat execution audit
- tool execution audit
- retrieval audit

### 17.2 Suggested Relational Tables

#### `ai_assistant`
- id
- assistant_code
- name
- description
- active
- tenant_scope
- created_at
- updated_at

#### `ai_assistant_prompt`
- id
- assistant_id
- prompt_type
- prompt_text
- version
- active
- created_at

#### `ai_assistant_model_policy`
- id
- assistant_id
- default_model
- routing_policy_json
- fallback_policy_json
- active

#### `ai_assistant_memory_policy`
- id
- assistant_id
- enabled
- store_type
- window_size
- ttl_minutes
- persist_history

#### `ai_assistant_rag_policy`
- id
- assistant_id
- enabled
- mode
- default_knowledge_base_id
- top_k
- similarity_threshold
- metadata_filter_json

#### `ai_assistant_tool_policy`
- id
- assistant_id
- tool_name
- enabled
- timeout_ms

#### `ai_knowledge_base`
- id
- knowledge_base_id
- name
- vector_store_type
- embedding_model
- connection_ref
- tenant_scope
- active

#### `ai_chat_session`
- id
- session_id
- assistant_code
- tenant_id
- user_id
- started_at
- last_activity_at
- status

#### `ai_chat_message`
- id
- session_id
- role
- message_text
- model_used
- created_at

#### `ai_chat_execution`
- id
- session_id
- request_id
- assistant_code
- selected_model
- prompt_version
- kb_id
- execution_status
- latency_ms
- input_tokens
- output_tokens
- created_at

#### `ai_tool_execution_audit`
- id
- execution_id
- tool_name
- status
- latency_ms
- request_summary
- response_summary
- created_at

#### `ai_retrieval_audit`
- id
- execution_id
- kb_id
- query_summary
- top_k
- returned_chunks
- retrieval_latency_ms
- created_at

---

## 18. API Endpoints

### 18.1 Chat API

#### `POST /api/v1/assistants/{assistantCode}/chat`

Purpose:
- synchronous chat response

Sample request:
```json
{
  "tenantId": "tid001",
  "sessionId": "sess-123",
  "userId": "u-456",
  "message": "Explain foreclosure charges for this product",
  "context": {
    "channel": "web",
    "locale": "en-IN"
  },
  "runtimeOverrides": {
    "knowledgeBaseId": "kb-loan-policy",
    "memoryMode": "DB_WINDOW",
    "toolNames": ["feeCalculator", "policyLookup"]
  }
}
```

### 18.2 Streaming Chat API

#### `POST /api/v1/assistants/{assistantCode}/stream`

Purpose:
- stream response tokens incrementally

### 18.3 Session History API

#### `GET /api/v1/assistants/{assistantCode}/sessions/{sessionId}/history`

Purpose:
- retrieve full conversation history for UI and audit use cases

### 18.4 Assistant Metadata API

#### `GET /api/v1/assistants/{assistantCode}`

Purpose:
- retrieve assistant metadata and active capabilities

---

## 19. Sequence Flows

### 19.1 Standard Chat Flow

```text
Client
  -> Conversation API
  -> Runtime Orchestrator
  -> Assistant Config Service (load config)
  -> Model Routing Service (select model)
  -> Prompt Assembly Service (build prompt)
  -> RAG Service (optional retrieval)
  -> Tool Registry (resolve allowed tools)
  -> Memory Strategy Layer (resolve memory)
  -> Spring AI ChatClient (execute)
  -> Audit/History Store (persist)
  -> Client (response)
```

### 19.2 Detailed Execution Flow

1. API receives chat request.
2. Security and request validation are applied.
3. Runtime Orchestrator loads assistant configuration.
4. Allowed runtime overrides are merged into execution context.
5. Model Routing Service selects model and parameters.
6. Prompt Assembly Service creates layered prompt.
7. RAG Service resolves knowledge base and retriever if enabled.
8. Memory Strategy Layer resolves memory mode.
9. Tool Registry resolves allowed tools.
10. Advisor chain is assembled.
11. `ChatClient` executes the request.
12. Tool calls and retrieval events are audited.
13. Response and execution metadata are persisted.
14. Final response is returned to the client.

### 19.3 Tool-Heavy Flow

1. User asks question requiring business data/action.
2. Runtime selects tool-enabled model route.
3. Tool Registry attaches allowlisted tools.
4. Model requests tool execution.
5. Tool execution layer enforces timeout and audit.
6. Tool result is returned to the model.
7. Final answer is generated and stored.

### 19.4 Grounded RAG Flow

1. Request is routed to an assistant with `REQUIRED_GROUNDED` policy.
2. KB is resolved.
3. Retrieval is executed.
4. If retrieval confidence is sufficient:
   - retrieved context is added to execution
   - response is generated with grounding rules
5. If retrieval confidence is insufficient:
   - controlled fallback answer is returned

---

## 20. Deployment and Packaging View

### 20.1 Deployment Style

Recommended deployment:
- Spring Boot application
- horizontally scalable stateless runtime nodes
- persistent DB for config/history/audit
- vector store externalized
- model provider integrations externalized by config

### 20.2 Suggested Maven Modules

```text
chatbot-platform-api
chatbot-platform-core
chatbot-platform-config
chatbot-platform-routing
chatbot-platform-rag
chatbot-platform-memory
chatbot-platform-tools
chatbot-platform-observability
```

### 20.3 Module Responsibilities

#### `chatbot-platform-api`
- REST contracts
- request/response DTOs

#### `chatbot-platform-core`
- orchestration
- shared domain interfaces

#### `chatbot-platform-config`
- config repositories
- assistant config resolution

#### `chatbot-platform-routing`
- complexity evaluation
- model selection

#### `chatbot-platform-rag`
- KB registry
- retriever resolution
- retrieval auditing

#### `chatbot-platform-memory`
- memory strategies
- history persistence

#### `chatbot-platform-tools`
- tool registry
- tool adapters
- future MCP integration

#### `chatbot-platform-observability`
- metrics
- tracing
- policy audit integration

---

## 21. Security and Governance

### 21.1 Security Controls

The design must enforce:
- authentication and authorization on APIs
- tenant-scoped access to configs and knowledge bases
- tool allowlisting
- restricted runtime overrides
- secret management for model/vector/tool credentials
- encryption for sensitive persisted data where required

### 21.2 Governance Controls

The design must support:
- prompt versioning
- policy versioning
- model routing audit
- tool invocation audit
- retrieval audit
- config activation/deactivation
- rollback of prompt/policy versions

### 21.3 Tenant Isolation

Tenant isolation applies to:
- assistant visibility
- knowledge base visibility
- chat memory
- chat history
- tool access rules

---

## 22. Observability

### 22.1 Metrics

The platform should capture:
- request count by assistant
- latency by assistant/model
- input/output token usage
- tool invocation counts
- tool failure rate
- retrieval latency
- retrieval success rate
- fallback counts
- memory repository failure rate

### 22.2 Tracing

Tracing should cover:
- API request span
- config resolution span
- model routing span
- retrieval span
- tool execution span
- model invocation span
- persistence span

### 22.3 Auditability

Audit records should include:
- assistant code
- tenant and session identifiers
- selected model
- selected KB
- selected tools
- prompt version
- routing decision
- tool and retrieval outcomes

---

## 23. Failure Handling Strategy

### 23.1 Failure Categories

- invalid assistant configuration
- unavailable model/provider
- retrieval failure
- missing knowledge base
- tool execution timeout/failure
- memory store failure
- persistence failure

### 23.2 Failure Policies

#### Model Failure
- use configured fallback model if allowed
- otherwise return controlled platform error

#### Retrieval Failure
- if assistant is optional-RAG, proceed without retrieval if policy allows
- if assistant is grounded-required, fail with controlled response

#### Tool Failure
- fail fast or continue based on assistant/tool policy
- always audit failure

#### Memory Failure
- degrade to stateless mode for low-risk assistants if policy allows
- otherwise fail

---

## 24. Scalability and Performance Considerations

### 24.1 Scalability

The runtime nodes should be horizontally scalable because:
- request execution is stateless at application level
- durable state is externalized to DB/vector store/provider APIs

### 24.2 Performance Optimizations

Potential optimizations:
- cache assistant config
- cache prompt/policy versions
- cache tool metadata
- connection pooling for vector store and DB
- async streaming where suitable
- selective audit payload truncation
- optional caching for stable retrieval results

### 24.3 Cost Controls

The design should support:
- model routing for cost-aware optimization
- per-assistant allowed model set
- max token policies
- optional request throttling
- per-tenant usage quotas in future phases

---

## 25. Recommended Initial Technical Choices

For an initial enterprise implementation aligned to this stack:

### 25.1 Core Runtime
- Java 21
- Spring Boot
- Spring AI
- Maven 3

### 25.2 Persistence
- PostgreSQL for configuration, chat history, and audit

### 25.3 Memory
- DB-backed memory for production assistants
- in-memory memory for low-risk/ephemeral assistants

### 25.4 Vector Store
- PGVector if minimizing operational sprawl is preferred
- alternative vector store only if scale/feature requirements justify it

### 25.5 Tools
- internal API tools initially
- MCP integration as an extensibility path

---

## 26. Future Enhancements

The architecture intentionally leaves room for:

- admin UI for assistant configuration
- prompt experimentation and A/B rollout
- hybrid retrieval and reranking
- query transformation pipelines
- answer groundedness scoring
- memory summarization
- dynamic cost governance
- multi-agent orchestration
- structured output workflows
- MCP-native enterprise tool federation

---

## 27. Design Decisions Summary

### 27.1 Key Decisions

1. Build **one generic chatbot runtime**, not multiple assistant-specific services.
2. Represent assistant specialization entirely through **configuration and runtime policies**.
3. Use **Spring AI `ChatClient`** as the execution boundary.
4. Use **Advisors** as the main orchestration extension mechanism.
5. Keep **chat memory** and **chat history** as separate concerns.
6. Use **policy-based model routing** rather than direct model selection by clients.
7. Use **runtime-selected knowledge bases** for RAG specialization.
8. Use **allowlisted tools** resolved at runtime from a central registry.
9. Provide **auditability and observability** as first-class platform features.
10. Keep design extensible for **MCP**, advanced RAG, and future governance needs.

### 27.2 Recommended Phase-1 Scope

Phase 1 should include:
- assistant config store
- prompt versioning
- basic model routing
- simple RAG with KB registry
- in-memory and DB memory modes
- tool allowlist resolution
- chat history persistence
- execution audit and metrics

---

## 28. Conclusion

This design provides a reusable, production-oriented, configurable chatbot platform built on Spring AI. The architecture is intentionally generic so that specialization happens through configuration rather than code duplication. The design supports RAG-based grounding, configurable tools, configurable memory, and policy-driven model selection while preserving governance, observability, and extensibility.

This approach is appropriate for enterprise environments that need:
- rapid onboarding of domain-specific assistants
- controlled use of prompts/tools/models
- runtime flexibility
- consistent audit and policy enforcement
- future expansion toward more advanced AI platform capabilities
