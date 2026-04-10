# Architectural Decisions and Change Log

All deviations from the initial diagrams must be documented here.

## ADR-001: Monolithic modular backend as migration starting point
- Date: 2026-04-09
- Status: Accepted
- Context: Existing system is desktop Java Swing; migration needs rapid parity while preserving diagram structure.
- Decision: Implement a modular monolith first with clear bounded packages (`auth`, `laundry`, `facilities`, `securityops`, `notifications`).
- Consequences:
  - Positive: Faster delivery, simpler operations initially, strong code ownership boundaries.
  - Tradeoff: Logical service boundaries are in-process, not independently deployable yet.

## ADR-002: JWT bearer token for session semantics
- Date: 2026-04-09
- Status: Accepted
- Context: Sequence diagram shows session/token management.
- Decision: Represent session token with signed JWT carrying user id and role.
- Consequences:
  - Positive: Stateless APIs, scalable horizontal deployment.
  - Tradeoff: Requires key rotation policy and token expiration handling.

## ADR-003: PostgreSQL with migration scripts
- Date: 2026-04-09
- Status: Accepted
- Context: Existing project already uses PostgreSQL.
- Decision: Keep PostgreSQL and introduce explicit SQL migration scripts under `src/main/resources/db/migration`.
- Consequences:
  - Positive: Controlled schema evolution and environment consistency.
  - Tradeoff: Migration discipline required for every schema change.

## ADR-004: Temporary open-route security during bootstrap
- Date: 2026-04-09
- Status: Accepted
- Context: Backend bootstrap needs rapid endpoint bring-up for integration while front-end is not yet attached.
- Decision: Keep route access open in phase 1 (`permitAll`) while still issuing JWT tokens in auth responses.
- Consequences:
  - Positive: Accelerates parallel API and frontend development.
  - Tradeoff: Not production-ready until phase 2 authorization filter and role guards are enabled.

## ADR-005: Assignment strategy v1 for facility reports
- Date: 2026-04-09
- Status: Accepted
- Context: Diagram requires worker assignment on report creation, but no optimization policy is specified.
- Decision: Use a deterministic first-available maintenance worker assignment in phase 1.
- Consequences:
  - Positive: Predictable behavior and simple implementation.
  - Tradeoff: Not load-balanced; replace with strategy abstraction in phase 2.

## ADR-006: Enforced JWT authentication for protected APIs
- Date: 2026-04-09
- Status: Accepted
- Context: Phase 1 intentionally allowed open routes for bootstrap.
- Decision: Move to stateless JWT authentication via request filter; permit only auth and health routes anonymously.
- Consequences:
  - Positive: Diagram-consistent session/token flow and protected service access.
  - Tradeoff: Requires frontend to include bearer tokens on protected calls.

## ADR-007: Ownership from authenticated principal, not request body
- Date: 2026-04-09
- Status: Accepted
- Context: Sequence diagrams assume actor identity from authenticated context.
- Decision: Resident identity for booking/report creation is derived from JWT principal, removing resident id from request payloads.
- Consequences:
  - Positive: Prevents cross-user spoofing and insecure direct object manipulation.
  - Tradeoff: Clients must authenticate before creating bookings/issues.

## Change Control
When changing structure, include:
1. Reason for change.
2. Diagram element impacted.
3. Code modules impacted.
4. Backward compatibility impact.
