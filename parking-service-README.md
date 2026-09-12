# Smart Parking Spot Finder — Spring Boot REST Microservice

One Spring Boot app exposing **three services**: parking spots (full CRUD),
zones (full CRUD), and a booking service that reuses the spot data instead of
duplicating it. All in-memory (no DB setup needed) so it runs anywhere with
just a JDK + Maven.

## Project layout

```
parking-service/
├── pom.xml
└── src/main/java/com/example/parkingservice/
    ├── ParkingServiceApplication.java     # main() — boots the embedded server
    ├── model/
    │   ├── ParkingSpot.java                # spot data + validation rules + available flag
    │   └── Zone.java                       # zone data + validation rules
    ├── service/
    │   ├── ParkingSpotService.java         # spot storage, full CRUD, book/release logic (also backs Booking)
    │   └── ZoneService.java                # zone storage, full CRUD
    ├── controller/
    │   ├── ParkingSpotController.java      # /api/spots — add, view, update, delete
    │   ├── ZoneController.java             # /api/zones — add, view, update, delete
    │   └── BookingController.java          # /api/bookings — composes ParkingSpotService, no storage of its own
    └── exception/
        ├── SpotNotFoundException.java
        ├── ZoneNotFoundException.java
        └── GlobalExceptionHandler.java     # turns ALL of the above errors into clean JSON + status codes
└── src/main/resources/application.properties
```

## How to run it

You need JDK 17+ and Maven installed.

```bash
cd parking-service
mvn spring-boot:run
```

It starts on **http://localhost:8080**. Three sample spots (ids 1–3, all in
Zone A/B) and two sample zones (ids 1–2) are pre-loaded so nothing looks
empty on first run.

If your laptop is tight on RAM: close other heavy apps before running
`mvn spring-boot:run`, and run it from a plain terminal rather than inside a
heavy IDE — that keeps memory use lowest for the demo.

## Endpoints

**Parking Spot service — full CRUD**

| Method | URL                   | Purpose                                  |
|--------|------------------------|--------------------------------------------|
| POST   | `/api/spots`           | Add a new parking spot                      |
| GET    | `/api/spots`           | View all spots                              |
| GET    | `/api/spots/{id}`      | View one spot by id                         |
| PUT    | `/api/spots/{id}`      | Update a spot's number/zone/vehicle type    |
| DELETE | `/api/spots/{id}`      | Delete a spot                               |

**Zone service — full CRUD**

| Method | URL                    | Purpose                    |
|--------|-------------------------|------------------------------|
| POST   | `/api/zones`            | Add a new zone                |
| GET    | `/api/zones`            | View all zones                |
| GET    | `/api/zones/{id}`       | View one zone by id           |
| PUT    | `/api/zones/{id}`       | Update a zone's name/description |
| DELETE | `/api/zones/{id}`       | Delete a zone                 |

**Booking service** (built on top of the spot catalog — no separate storage)

| Method | URL                          | Purpose                                          |
|--------|-------------------------------|-----------------------------------------------------|
| PUT    | `/api/bookings/{id}/book`     | Mark a spot as occupied (409 if already booked)      |
| PUT    | `/api/bookings/{id}/release`  | Mark a spot as free (409 if it wasn't booked)        |
| GET    | `/api/bookings/available`     | List spots currently free                            |

## Sample request bodies

Add/update a spot (`available` is server-controlled — don't send it):
```json
{ "spotNumber": "C1", "zone": "Zone C", "vehicleType": "EV" }
```

Add/update a zone:
```json
{ "name": "Zone C", "description": "Rooftop level, EV charging points" }
```

## Testing with Postman

Import **`parking-service-postman.json`** into Postman (File → Import) and
run the requests in order — they're grouped into three folders matching the
three services below.

**Parking Spot service**
1. **Add a spot**: `POST /api/spots` with the sample body → `201`, note the `id`.
2. **View all**: `GET /api/spots` → confirm the new spot alongside the 3 seeded ones.
3. **View one**: `GET /api/spots/1` → `200`.
4. **Update**: `PUT /api/spots/1` with a changed `zone` → `200`, confirm the change.
5. **Invalid input**: `POST /api/spots` with a blank `spotNumber` → `400` with the `details` map.
6. **Not found**: `GET /api/spots/999` → `404`.
7. **Delete**: `DELETE /api/spots/3` → `204`, then `GET /api/spots/3` → `404` to confirm it's gone.

**Zone service**
8. **Add a zone**: `POST /api/zones` with the sample body → `201`.
9. **View all**: `GET /api/zones` → confirm 2 seeded + your new one.
10. **Update**: `PUT /api/zones/1` with a changed `description` → `200`.
11. **Delete**: `DELETE /api/zones/2` → `204`.

**Booking service**
12. **Book**: `PUT /api/bookings/1/book` → `200`, `available: false`.
13. **Available list**: `GET /api/bookings/available` → confirm spot 1 no longer appears.
14. **Double-book (conflict)**: repeat step 12 → `409`.
15. **Release**: `PUT /api/bookings/1/release` → `200`, `available: true`.

Screenshot each response (status code + body) — that's your evidence the APIs
actually work, in place of a PPT/video.

## Viva talking points

**What each endpoint does**
- `POST /api/spots`: validates the body, assigns an id, forces
  `available = true` (a client can't add a pre-booked spot), stores it, `201`.
- `PUT /api/spots/{id}`: updates only the descriptive fields (number, zone,
  vehicle type) — deliberately can't touch `available` through this endpoint,
  so a spot's occupied/free state only ever changes through Booking.
- `DELETE /api/spots/{id}`: removes the spot entirely; `404` if the id doesn't exist.
- Zone endpoints mirror the spot endpoints exactly, backed by their own
  `ZoneService` and `ZoneNotFoundException`.
- `PUT /api/bookings/{id}/book` / `/release`: `PUT` because they mutate the
  state of an existing resource, not create anything.

**Why three "services" but only two `@Service` classes**
`BookingController` has no storage of its own — it injects `ParkingSpotService`
and calls `bookSpot`/`releaseSpot`/`getAvailableSpots` on it. The `available`
flag lives in exactly one place (on the `ParkingSpot` object inside
`ParkingSpotService`'s map), so Booking can never disagree with the Spot
catalog about whether a spot is free.

**What happens on invalid input**
`@Valid` runs the `@NotBlank` checks on `ParkingSpot`/`Zone` before the
controller method body executes; a failure throws
`MethodArgumentNotValidException`, caught by `GlobalExceptionHandler` and
turned into `400` with a field-by-field map. A missing id on GET/PUT/DELETE
throws a custom `*NotFoundException` → `404`. A booking action that conflicts
with the spot's current state throws `IllegalStateException` → `409 Conflict`
— not `400`, because the request itself is well-formed, it just clashes with
the resource's current state.

**Likely follow-up questions to prepare for**
- Why does `DELETE` return `204` instead of `200`? → `204 No Content` is the
  standard response for a successful delete — there's no body to return.
- Why is `available` excluded from the `PUT /api/spots/{id}` update? → it's
  state managed by a different concern (bookings); letting a generic "edit
  spot details" call also flip occupancy would create two ways to change the
  same flag and risk them disagreeing.
- Why `409` for double-booking instead of `400`? → `400` means the request
  itself is malformed; `409` means it's well-formed but conflicts with the
  resource's current state — exactly what a double-booking is.
