# Sahayak-Mesh X

> **Autonomous Offline-First Android Communication & Emergency Coordination Platform**

Sahayak-Mesh X is built for mission-critical field communication when cellular towers and internet infrastructure are degraded or unavailable (e.g., natural disasters, remote campuses, large-scale public events, search & rescue).

---

## 🌟 Core Pillars

1. **Zero-Internet Communication**: Opportunistic peer-to-peer and store-and-forward mesh routing over Bluetooth Low Energy, Wi-Fi Direct, and Google Nearby Connections.
2. **End-to-End Cryptography (E2EE)**: Asymmetric cryptography ensures relays carry ciphertext without being able to eavesdrop on private one-to-one communication.
3. **Signed Command Broadcasting & RBAC**: Evacuation orders and emergency bulletins are digitally signed by authenticated coordinators to prevent panic injection or impersonation.
4. **Structured Incident Management**: SOS alerts, triage, responder dispatch, duplicate incident detection, and human acknowledgment tracking.
5. **Honest Delivery Semantics**: 8 explicit message states (`LOCAL_QUEUED` -> `HUMAN_ACKNOWLEDGED`). A message is never claimed as delivered without cryptographic proof from the recipient device.

---

## 📂 Repository Structure

```
├── app/                 # Android application module & entry point
│   ├── src/main/java/org/sahayak/mesh/
│   │   ├── core/model/       # Platform-independent entities & domain values
│   │   ├── core/protocol/    # Packet envelopes, serialization & validation
│   │   ├── core/security/    # Ed25519 & X25519 cryptographic primitives
│   │   ├── domain/messaging/ # Message lifecycle, queues, deduplication
│   │   ├── domain/mission/   # Missions, RBAC, signed broadcasts
│   │   ├── domain/emergency/ # SOS alerts, responder assignment
│   │   ├── data/database/    # Room database, entities, DAOs & migrations
│   │   ├── data/repository/  # Repository implementations
│   │   ├── transport/api/    # Transport & peer abstractions
│   │   ├── transport/relay/  # Store-and-forward routing & cache
│   │   └── ui/               # Jetpack Compose UI (Material 3)
├── docs/                # Architecture, Protocols, Threat Model, ADRs
├── build.gradle.kts     # Root build configuration
├── settings.gradle.kts  # Gradle settings
└── AGENTS.md            # Persistent engineering rules & invariants
```

---

## 🛠️ Build & Verification Instructions

### Prerequisites
- JDK 17+ (Adoptium Temurin OpenJDK 17 recommended)
- Android SDK (API Level 34+, Build Tools 34.0.0+)

### Running Unit & Protocol Tests
```bash
./gradlew testDebugUnitTest
```

### Building the Debug APK
```bash
./gradlew assembleDebug
```

---

## 📋 Documentation Links
- [Architecture & Data Flows](docs/ARCHITECTURE.md)
- [Wire & Message Protocol](docs/MESSAGE-PROTOCOL.md)
- [Security Threat Model](docs/THREAT-MODEL.md)
- [Test Plan & Verification Matrix](docs/TEST-PLAN.md)
- [Architecture Decision Records (ADR)](docs/DECISIONS.md)
- [Feature Implementation Status](docs/FEATURE-STATUS.md)
- [Hardware & Field Test Report](docs/DEVICE-TEST-REPORT.md)
