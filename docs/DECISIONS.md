# Sahayak-Mesh X — Architecture Decision Records (ADRs)

## ADR-001: Choice of Local Transport Framework

- **Status**: Accepted
- **Context**: Sahayak-Mesh X requires offline peer-to-peer data exchange between Android devices without access to cellular towers or Wi-Fi routers.
- **Decision**: Introduce a pluggable `MeshTransport` interface with Google Nearby Connections API (`Strategy.P2P_CLUSTER`) as the primary wireless engine, paired with an in-memory `SimulatedMeshTransport` for automated JVM unit and integration testing.
- **Rationale**:
  - `P2P_CLUSTER` supports M-to-N mesh topologies where any device can advertise and discover simultaneously.
  - Automatically manages Wi-Fi Direct, Bluetooth Low Energy (BLE), and Bluetooth Classic radio handshakes.
  - Abstracting behind `MeshTransport` decouples domain messaging logic from Google Play Services, enabling future alternative transports (e.g., pure BLE L2CAP, LoRa, or Wi-Fi Aware).

---

## ADR-002: End-to-End Cryptography and Identity System

- **Status**: Accepted
- **Context**: Intermediate relays forward packets. They must be unable to read private communication or forge coordinator announcements.
- **Decision**:
  - Use standard Ed25519 for digital signatures (Identity authentication, mission tickets, coordinator announcements).
  - Use X25519 (ECDH) + ChaCha20-Poly1305 / AES-GCM AEAD for one-to-one message encryption.
  - Device master keys are generated locally and stored securely with Android Keystore.
- **Rationale**:
  - Avoids custom cryptography.
  - Ed25519 signatures are 64 bytes and X25519 public keys are 32 bytes, minimizing mesh packet overhead.
  - ChaCha20-Poly1305 provides high throughput on mobile processors even without hardware AES acceleration.

---

## ADR-003: Message State Representation & Delivery Tracking

- **Status**: Accepted
- **Context**: In an asynchronous store-and-forward mesh, delivery is non-linear and per-recipient. A packet being accepted by a relay does NOT mean it reached the recipient.
- **Decision**:
  - Model message state using discrete verifiable states:
    `LOCAL_QUEUED`, `TRANSMITTED`, `RECEIVED_BY_DEVICE`, `READ_BY_RECIPIENT`, `HUMAN_ACKNOWLEDGED`, `FAILED`, `EXPIRED`, `CANCELLED`.
  - For broadcasts, track delivery status per recipient in a separate `BroadcastRecipientRecord` database table.
- **Rationale**:
  - Accurately captures reality: a broadcast is never marked "delivered" in aggregate when only 3 out of 10 participants received it.
  - Receipts are cryptographically signed by recipient device keys before propagation back to sender.

---

## ADR-004: Local Storage and Persistence

- **Status**: Accepted
- **Context**: The app must survive process death, device reboots, and operate completely offline.
- **Decision**: Use Room Database (SQLite) with strict repository abstraction and StateFlow reactive queries.
- **Rationale**:
  - Full ACID compliance and transactional safety for outbox/inbox queues.
  - Strongly typed Kotlin coroutine integration (`Flow<List<Message>>`).
  - Schema versioning with migration verification.
