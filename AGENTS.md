# AGENTS.md — Persistent Engineering Rules for Sahayak-Mesh X

This repository contains **Sahayak-Mesh X**, an offline-first Android communication and emergency coordination platform designed for campuses, public events, disaster preparedness, and field coordination with unreliable connectivity.

All future AI agents and contributors working in this codebase must strictly adhere to the following principles.

---

## 1. Golden Rules of Verification & Claims

1. **Evidence-backed claims only**: Never claim a feature works, a test passed, a message was delivered, or a cryptographic property holds without real, reproducible output.
2. **Never simulate real evidence**: In logs, dashboards, and test reports, always distinguish clearly between:
   - Real physical-device tests
   - Local unit/integration tests
   - In-memory mock/simulated transport tests
   - Demo mock data
3. **No linear assumptions about delivery**: Delivery is per-recipient. A message is never marked as delivered just because a relay acknowledged receipt of the packet.
4. **State Machine Integrity**: Respect the explicit message states:
   - `LOCAL_QUEUED`
   - `TRANSMITTED`
   - `RECEIVED_BY_DEVICE`
   - `READ_BY_RECIPIENT`
   - `HUMAN_ACKNOWLEDGED`
   - `FAILED`
   - `EXPIRED`
   - `CANCELLED`
5. **No custom cryptography**: Never invent cryptographic primitives. Use proven, reviewed libraries and standards (e.g., Noise Protocol Framework / Signal Protocol / Libsodium / Android Keystore backed keys).

---

## 2. Architectural Boundaries & Discipline

1. **Package Separation**:
   - `app`: Application setup, DI, lifecycle wiring.
   - `core/model`: Pure Kotlin data models and value types (zero Android UI/Bluetooth dependencies).
   - `core/protocol`: Versioned message envelopes, serializers, validation logic.
   - `core/security`: Cryptographic abstractions, key management, signature verification.
   - `domain/identity`: Device identity, contact trust, authorization.
   - `domain/messaging`: Message dispatch, deduplication, retry policies, state transitions.
   - `domain/mission`: Mission lifecycle, role-based authorization, broadcasts.
   - `domain/emergency`: SOS alerts, responder assignment, escalation workflows.
   - `data/database`: Room entities, DAOs, type converters, migrations.
   - `data/repository`: Concrete repositories implementing domain interfaces.
   - `transport/api`: Generic peer and transport interfaces (`MeshTransport`, `PeerDiscovery`, `TransportPacket`).
   - `transport/nearby`: Google Nearby Connections implementation.
   - `transport/relay`: Store-and-forward routing, cache eviction, hop limit management.
   - `ui`: Jetpack Compose screens, ViewModels, Material 3 theme.
2. **Strict UI Separation**: The UI layer must never directly touch Room DAOs, Bluetooth, sockets, or cryptographic primitives. All actions must flow through domain use cases and repositories.
3. **Transport Independence**: Transports must be modular and hot-swappable. The messaging engine operates against `MeshTransport` abstractions.

---

## 3. Toolchain & Dependencies

- **Language**: Kotlin 2.x
- **Build**: Gradle with Kotlin DSL (`build.gradle.kts`)
- **Android Target**: minSdk 26 (Android 8.0 Oreo), targetSdk 35
- **UI**: Jetpack Compose + Material 3
- **Local Storage**: Room with versioned migrations
- **Background Tasks**: AndroidX WorkManager
- **Serialization**: Versioned binary/JSON format (schema enforced with packet version field)
- **Key Storage**: Android Keystore (`AndroidKeyStore` provider)

---

## 4. Documentation & Milestone Checklists

Every completed milestone must update:
1. `docs/FEATURE-STATUS.md` — What is verified vs. unverified.
2. `docs/DEVICE-TEST-REPORT.md` — Real hardware testing log.
3. Implementation checklist in `README.md`.
