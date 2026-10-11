# Sahayak-Mesh X — Feature Verification & Implementation Status

In accordance with our strict verification principles, every feature is explicitly classified into one of four states:
- **VERIFIED**: Proven with executed unit/integration/instrumented tests or real device logs.
- **IMPLEMENTED (UNVERIFIED)**: Code written and built, but awaiting specific hardware or automated test validation.
- **SIMULATED**: Tested against in-memory or simulated mesh harness.
- **PLANNED**: Not yet implemented.

---

## Current Status Matrix (Post-Review Baseline)

| Capability / Feature | Status | Verification Evidence / Details |
| :--- | :--- | :--- |
| **Project Build Baseline** | **VERIFIED** | Gradle 8.10.2 + AGP 8.5.2 + OpenJDK 17 + Android 34 SDK. `assembleDebug` passed, generated `app-debug.apk` (22MB). |
| **Room Local Persistence Across Restarts** | **VERIFIED** | Verified with Robolectric in `RoomPersistenceTest`: `LOCAL_QUEUED` message survives full database close and re-open. |
| **Message State Model & Invariants** | **VERIFIED** | 8-state model. Verified in `MessageStateTest` (7 tests passing), including prevention of delivered/read messages expiring. |
| **Wire Protocol & Validation** | **VERIFIED** | `PacketEnvelope` v1 with canonical hashing, size bounds (64KB), hop limits, and inverted lifetime checks. Verified in `ProtocolValidatorTest` (7 tests passing). |
| **Mission Broadcasts & RBAC** | **VERIFIED** | Ed25519 coordinator signatures, role enforcement, payload type validation, and mission expiry checks. Verified in `MissionAuthorizerTest` (6 tests passing). |
| **Deduplication Engine** | **VERIFIED (In-Memory)** | Thread-safe LRU cache with 48h TTL. Verified in `DeduplicationFilterTest` (3 tests passing). Note: In-memory only; cleared on app restart (unresolved risk). |
| **Cryptographic Engine (Interim)** | **VERIFIED (Interim)** | Ed25519 signing + X25519 + HKDF-SHA256 + AES-256-GCM AEAD with AAD binding. Verified in `CryptoEngineTest` (6 tests passing). **AUDIT FINDING**: Ad-hoc custom hybrid assembly; requires migration to standard Google Tink HPKE / Noise Protocol. |
| **Multi-hop Store-and-Forward (Simulation)** | **SIMULATED** | 3-node in-memory routing (A -> Relay B -> C) where Relay B cannot decrypt payload. Verified in `SimulatedMeshRoutingTest` (1 test passing). |
| **Google Nearby Connections Radio Transport** | **PLANNED (0% Implemented)** | Currently only an abstract interface (`MeshTransport`) and in-memory mock (`SimulatedMeshTransport`). No concrete `NearbyConnectionsClient` code written yet. |
| **Physical Nearby RF Hardware** | **UNVERIFIED** | Awaiting attached physical Android test devices. Documented in `docs/DEVICE-TEST-REPORT.md`. |
| **Scale & 1,000 Peer Mesh** | **PLANNED** | High-volume simulation harness. Zero unverified physical claims. |

---

## Test Verification Summary
- **Total Unit, Persistence & Regression Tests Executed**: 31
- **Passed**: 31
- **Failed**: 0
- **Skipped**: 0
- **Test Execution Suites**:
  1. `MessageStateTest` (7/7 passed)
  2. `ProtocolValidatorTest` (7/7 passed)
  3. `CryptoEngineTest` (6/6 passed)
  4. `RoomPersistenceTest` (1/1 passed)
  5. `DeduplicationFilterTest` (3/3 passed)
  6. `MissionAuthorizerTest` (6/6 passed)
  7. `SimulatedMeshRoutingTest` (1/1 passed)
- **Build Output**: `app/build/outputs/apk/debug/app-debug.apk` (22,001,143 bytes)
