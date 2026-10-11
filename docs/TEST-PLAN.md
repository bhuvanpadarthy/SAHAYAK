# Sahayak-Mesh X — Quality Assurance & Test Strategy

## 1. Testing Philosophy & Verification Levels

In accordance with our core engineering rules:
- **Zero False Claims**: A test is never marked as passed unless actually executed and verified with logs.
- **Physical vs. Simulated Separation**: Simulated mesh topologies and in-memory transports are strictly differentiated from physical Bluetooth/Wi-Fi Direct device tests.

---

## 2. Test Pyramid & Scope

### Level 1: Unit Tests (JVM Local, fast, deterministic)
- **Message State Transitions**:
  - Valid transitions: `LOCAL_QUEUED` -> `TRANSMITTED` -> `RECEIVED_BY_DEVICE` -> `READ_BY_RECIPIENT` -> `HUMAN_ACKNOWLEDGED`.
  - Disallowed transitions: Once `EXPIRED` or `FAILED`, invalid transitions must throw or be rejected.
  - Per-recipient state independence in broadcast missions.
- **Protocol Envelopes & Serialization**:
  - Envelope parsing, magic header verification, forward/backward schema compatibility.
  - Rejecting malformed packets, oversized packets (>64KB), and tampering detection.
  - Canonical header byte serialization and deterministic hash matching.
- **Role-Based Access Control (RBAC)**:
  - Unauthorized participants attempting coordinator broadcasts must be rejected.
  - Valid coordinator Ed25519 signature checks.
- **Deduplication & Expiry**:
  - Processing duplicate packet IDs must return idempotent NOOP without re-queuing or forwarding.
  - Packets with `expiresAtMs < now` are evicted and dropped immediately.
  - Clock skew handling: Tolerance window of +/- 15 minutes.
- **Store-and-Forward Relay Policies**:
  - Hop count decrement and ceiling (`hopCount < hopLimit`).
  - Priority queue fairness: SOS emergency packets are dispatched first; non-emergency throttled.

### Level 2: Database Persistence & Repository Tests (Room SQLite)
- Migrations: Safe schema upgrades without data loss.
- Outbox Queue: Messages remain in `LOCAL_QUEUED` across application kills/restarts.
- Idempotent upserts on incoming delivery acknowledgments.

### Level 3: Transport & Multi-Peer Simulation
- Simulated in-memory peer discovery and mesh topology (e.g. Node A -> Relay B -> Node C).
- Verifying Relay B forwards ciphertext without possessing the private key to decrypt payload.
- Verifying delivery ACK returns from Node C through Relay B back to Node A.

### Level 4: Instrumented & Device Field Tests
- Google Nearby Connections advertising and discovery on physical hardware.
- Real-world Wi-Fi Direct / BLE peer throughput and packet loss in RF-congested environments.
- Battery consumption under background beaconing and relay duty cycles.

---

## 3. Test Execution Matrix

| Test Suite | Target Component | Environment | Execution Command |
| :--- | :--- | :--- | :--- |
| `MessageStateTest` | `domain/messaging` | JVM (JUnit 5 / JUnit 4) | `./gradlew testDebugUnitTest --tests *MessageState*` |
| `ProtocolValidationTest` | `core/protocol` | JVM | `./gradlew testDebugUnitTest --tests *Protocol*` |
| `MissionRBACTest` | `domain/mission` | JVM | `./gradlew testDebugUnitTest --tests *Mission*` |
| `DeduplicationTest` | `transport/relay` | JVM | `./gradlew testDebugUnitTest --tests *Deduplication*` |
| `RoomMigrationTest` | `data/database` | JVM / Robolectric / Android | `./gradlew testDebugUnitTest --tests *Migration*` |
| `SimulatedMeshRoutingTest` | `transport/simulation`| JVM | `./gradlew testDebugUnitTest --tests *Routing*` |
| `DeviceNearbyTest` | `transport/nearby` | 2+ Physical Android Devices | Manual test script documented in `DEVICE-TEST-REPORT.md` |
