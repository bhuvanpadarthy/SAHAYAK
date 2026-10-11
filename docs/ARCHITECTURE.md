# Sahayak-Mesh X — Architecture & System Design

## 1. System Overview

Sahayak-Mesh X is an offline-first resilient communication and emergency coordination system built for Android devices operating in environments with degraded, intermittent, or absent cellular/internet infrastructure.

The core design centers around an **asynchronous store-and-forward mesh topology** layered over interchangeable local transport adapters (e.g., Google Nearby Connections Star/Cluster topologies, Wi-Fi Direct, Bluetooth Low Energy).

```
+-------------------------------------------------------------------------+
|                              PRESENTATION                               |
|          Jetpack Compose UI (Material 3) + AndroidX ViewModels          |
|    - Dashboard   - Direct Chats   - Missions/Broadcasts   - SOS Center   |
|    - Relay Diagnostics            - Venue Maps & Safe Routes            |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                              DOMAIN LAYER                               |
|  - Identity & Contact Trust     - Messaging Engine & Dispatcher         |
|  - Mission Authorization & RBAC - Emergency Incident Lifecycle         |
|  - Deduplication & Expiry       - Multi-hop Store-and-Forward Routing   |
+-------------------------------------------------------------------------+
                    |                                   |
                    v                                   v
+--------------------------------------+ +--------------------------------+
|             DATA LAYER               | |         SECURITY CORE          |
| - Room Database (Encrypted/Offline)  | | - Ed25519 & X25519 Identity    |
| - Durable Outbox / Inbox Queues      | | - End-to-End Encryption (E2EE) |
| - State Transition Audit Logs        | | - Android Keystore Integration |
+--------------------------------------+ +--------------------------------+
                    |
                    v
+-------------------------------------------------------------------------+
|                            TRANSPORT LAYER                              |
|           MeshTransport Abstraction (Replaceable Adapters)              |
|  +--------------------+  +--------------------+  +--------------------+ |
|  | Nearby Connections |  | Wi-Fi Direct (P2P) |  | Loopback/Mock Test | |
|  +--------------------+  +--------------------+  +--------------------+ |
+-------------------------------------------------------------------------+
```

---

## 2. Package Boundaries & Responsibilities

1. **`app`**:
   - `SahayakApp`: Application class, dependency container, WorkManager configuration.
2. **`core/model`**:
   - Pure Kotlin data structures with zero framework dependencies.
   - Core entities: `DeviceId`, `Contact`, `Message`, `Mission`, `Incident`, `MessageState`.
3. **`core/protocol`**:
   - Binary/JSON serialization schemas, versioning tags, and packet envelopes.
   - Envelopes: `PacketEnvelope`, `PayloadType`, `DeliveryAckPayload`, `HeartbeatPayload`.
4. **`core/security`**:
   - Cryptographic primitives and abstractions.
   - Ed25519 signing for identity verification and coordinator broadcasts.
   - X25519 Diffie-Hellman + ChaCha20-Poly1305 / AES-256-GCM for E2EE payloads.
   - Android Keystore secure storage for hardware-backed master identity keys.
5. **`domain/identity`**:
   - Device key generation, fingerprint calculation, contact exchange via QR / nearby greeting.
   - Trust state machine: `UNKNOWN`, `UNVERIFIED`, `VERIFIED_IN_PERSON`, `BLOCKED`, `REVOKED`.
6. **`domain/messaging`**:
   - Message transmission state machine (`LOCAL_QUEUED` -> `TRANSMITTED` -> `RECEIVED_BY_DEVICE` -> `READ_BY_RECIPIENT` -> `HUMAN_ACKNOWLEDGED`).
   - Deduplication filter via message UUID and content hash cache.
   - Outbox dispatcher with exponential backoff and battery-aware throttling.
7. **`domain/mission`**:
   - Role-Based Access Control (RBAC): Coordinator, Responder, Volunteer, Staff, Relay, Visitor.
   - Digital signature verification of all coordinator announcements before acceptance.
   - Targeted broadcasts (by role, zone, building/floor).
8. **`domain/emergency`**:
   - Structured SOS alerts: Medical, Evacuation, Accessibility, General Hazard.
   - Escalation timeouts and acknowledgment tracking.
   - Safe merge suggestions for duplicate incident reports.
9. **`data/database`**:
   - Room Database with schemas for Messages, Recipients, Missions, Contacts, Incidents.
   - Fully offline-first: UI reacts strictly to StateFlow from local Room tables.
10. **`transport/api`**:
    - `MeshTransport`: Unified interface for `startAdvertising()`, `startDiscovery()`, `sendPacket()`, `onPacketReceived()`.
    - `PeerSession`: Peer metadata, RSSI, link quality, last seen timestamp.
11. **`transport/relay`**:
    - Store-and-forward packet caching.
    - TTL / Hop-limit decrementing (default max hops: 5).
    - Cache eviction (LRU + priority-based retention: SOS packets never evicted first).
12. **`ui`**:
    - Material 3 theme with Dark/Light mode and High-Contrast Emergency Mode.
    - Navigation Compose orchestrating main views and coordinator workflows.

---

## 3. Data Flow

### A. Outgoing Private Message
1. User enters text in UI.
2. `MessagingViewModel` invokes `SendMessageUseCase`.
3. `SecurityEngine` encrypts plaintext payload with recipient's public key (X25519 / AEAD).
4. `MessageRepository` persists message into Room Outbox with status `LOCAL_QUEUED`.
5. `MeshDispatcher` queries available peers via `TransportManager`.
6. If direct peer connected -> sends `PacketEnvelope` directly.
7. If not connected -> marks packet eligible for store-and-forward opportunistic relaying.
8. State transitions to `TRANSMITTED` upon successful local radio transfer.

### B. Incoming Packet Handling
1. `MeshTransport` receives raw byte buffer.
2. `ProtocolParser` validates magic bytes, version compatibility, and size bounds (max 64KB for control/messages).
3. If packet is targeted to *this* device:
   - If encrypted: `SecurityEngine` decrypts with private identity key.
   - Verifies sender signature.
   - Writes to Room Inbox; emits `RECEIVED_BY_DEVICE` ACK back towards sender.
4. If packet is targeted to *another* device (Relay mode):
   - Verifies hop count < max limit and packet has not expired.
   - Checks if packet ID is already seen (deduplication check).
   - If valid, stores in `RelayCache` and schedules forwarding to newly discovered peers.
