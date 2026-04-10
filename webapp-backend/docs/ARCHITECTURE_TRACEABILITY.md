# Architecture Traceability (Diagram-Preserved)

## Intent
This document translates the provided diagrams into implementation constraints for the web application.
The diagrams are treated as the source of truth. Any changes must be recorded in `docs/ARCHITECTURAL_DECISIONS.md`.

## Diagram-to-Implementation Mapping

### 1) Layered deployment diagram
Expected layers from diagram:
- Client Devices (Resident/Block Rep, Admin/Security Guard)
- Edge/DMZ (TLS, rate limiting, routing)
- Application Server Cluster (Auth, domain services, notifications)
- Database Server (PostgreSQL)

Implementation mapping in this backend:
- Edge concerns mapped to API boundary and security filters:
  - `auth.web`, `common.security`, rate-limit hooks (extensible)
- Application services mapped to module packages:
  - `auth`, `residents`, `laundry`, `facilities`, `securityops`, `notifications`
- Data access mapped to repository interfaces and JDBC implementation:
  - `*.repository`, `*.repository.jdbc`

### 2) Class diagram (OO domain model)
Core abstraction preserved:
- `User` abstraction with role specialization semantics.
- Issue/report and booking entities represented as rich domain objects.

Implementation mapping:
- `common.domain.user.UserAccount`
- `common.domain.user.UserRole`
- `laundry.domain.LaundryBooking`
- `facilities.domain.FacilityIssueReport`
- `securityops.domain.SecurityIssueReport`

### 3) Sequence diagram: Login
Flow preserved:
1. Client submits credentials
2. Auth service validates against repository
3. Session token generated on success
4. Unauthorized response on failure

Implementation mapping:
- Endpoint: `POST /api/v1/auth/login`
- Service: `auth.service.AuthService`
- Token handling: `auth.service.TokenService`

### 4) Sequence diagram: Book Laundry Appointment
Flow preserved:
1. Availability checked for date/slot/machine
2. Booking created if available
3. Confirmation notification emitted
4. Optional modify/cancel represented via future endpoints

Implementation mapping:
- Endpoint: `POST /api/v1/laundry/bookings`
- Endpoint: `GET /api/v1/laundry/availability`
- Service: `laundry.service.LaundryService`
- Notification integration point: `notifications.service.NotificationService`

### 5) Sequence diagram: Report Facility Issue
Flow preserved:
1. Resident submits issue
2. Report persisted
3. Worker assignment attempted
4. Status updated and notification emitted

Implementation mapping:
- Endpoint: `POST /api/v1/facilities/issues`
- Endpoint: `GET /api/v1/facilities/issues/{issueId}`
- Service: `facilities.service.FacilityIssueService`

## Architectural Constraints
- Domain objects remain independent from HTTP framework concerns.
- Service layer orchestrates use-cases and transaction boundaries.
- Repository layer owns persistence details.
- API contracts are versioned under `/api/v1`.
- Role-based authorization is enforced per route.

## Planned Extensions (diagram-consistent)
- Separate gateway deployment and mTLS at edge.
- Extract `notifications` to independent service.
- Add worker assignment strategy abstractions.
