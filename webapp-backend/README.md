# GAH Facilities Web Backend

Production-oriented Spring Boot backend scaffold that preserves the structure described in the architecture and sequence diagrams.

## Key characteristics
- Modular monolith with explicit service boundaries.
- OO domain modeling and service orchestration.
- PostgreSQL persistence via repository interfaces.
- JWT-based authentication and role-aware authorization hooks.
- Traceability documentation from diagram to code.

## Modules
- `auth`
- `residents`
- `laundry`
- `facilities`
- `securityops`
- `notifications`
- `common`

## Run
1. Configure environment variables:
   - `GAH_DB_URL`
   - `GAH_DB_USER`
   - `GAH_DB_PASSWORD`
   - `GAH_JWT_SECRET`
2. Start PostgreSQL.
3. Run:
   - `mvn spring-boot:run`

## API base path
- `/api/v1`

## Implemented API surface (phase 1)
- Auth (Login use-case)
   - `POST /api/v1/auth/register`
   - `POST /api/v1/auth/login`
- Laundry (Book Laundry Appointment use-case)
   - `GET /api/v1/laundry/availability`
   - `POST /api/v1/laundry/bookings`
   - `DELETE /api/v1/laundry/bookings/{bookingId}`
- Facilities (Report Facility Issue use-case)
   - `POST /api/v1/facilities/issues`
   - `GET /api/v1/facilities/issues/{issueId}`

## Sequence alignment
- Login sequence: mapped to `auth` controller/service/repository flow and JWT emission.
- Laundry booking sequence: mapped to availability check, booking persistence, and confirmation notification.
- Facility report sequence: mapped to report creation, assignment attempt, status transition, and notifications.

## Notes
- This phase implements a modular monolith that preserves service boundaries from the diagrams.
- Phase 2 security is now active: JWT authentication is required on protected routes.
- Ownership-sensitive operations derive identity from token principal, not request body user ids.

## Security model
- Public routes:
   - `POST /api/v1/auth/register`
   - `POST /api/v1/auth/login`
   - `GET /api/v1/health`
- Authenticated routes:
   - all remaining `/api/v1/**` routes
- Role-guarded routes (method security):
   - Laundry: `RESIDENT`, `BLOCK_REPRESENTATIVE`, `ADMIN`
   - Facility issues: create for `RESIDENT`, `BLOCK_REPRESENTATIVE`, `ADMIN`; read also includes `MAINTENANCE_WORKER`

## Docs
- Architecture traceability: `docs/ARCHITECTURE_TRACEABILITY.md`
- Architectural decisions: `docs/ARCHITECTURAL_DECISIONS.md`
