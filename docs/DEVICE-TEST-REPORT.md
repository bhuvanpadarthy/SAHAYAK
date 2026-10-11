# Sahayak-Mesh X — Hardware & Device Test Report

This document records physical device field testing results, OS versions, radio behaviors, and real hardware test runs.

---

## 1. Connected Devices Status

- **Host Machine**: Windows 11 / 10.0.26300
- **ADB Toolchain**: Android Debug Bridge version 1.0.41 (v37.0.1)
- **Connected ADB Physical Devices**: None currently attached to host.
- **Current Verification Environment**: JVM local test suites and in-memory multi-node mesh simulator.

---

## 2. Physical Device Testing Protocols (To be executed upon hardware attachment)

### Test Profile A: Two-Device Direct P2P (Phase 2 Validation)
- **Objective**: Verify direct outbox-to-inbox delivery between Device A and Device B with Cellular and Wi-Fi Internet disabled.
- **Requirements**:
  - 2 Android devices (API >= 26) with Bluetooth and Wi-Fi enabled.
  - Airplane mode turned ON, then Bluetooth and Wi-Fi turned ON (No SIM / No AP connection).
- **Test Steps**:
  1. Launch Sahayak-Mesh X on Device A and Device B.
  2. Exchange contact public keys via QR code scanning.
  3. Device A sends private message "Alpha to Bravo check".
  4. Verify state transitions on Device A: `LOCAL_QUEUED` -> `TRANSMITTED` -> `RECEIVED_BY_DEVICE`.
  5. Inspect logcat on Device B to ensure plaintext is received and decrypted only by Device B.

### Test Profile B: 3-Node Store-and-Forward Relay (Phase 4 Validation)
- **Objective**: Verify packet forwarding through Relay B when Device A and Device C are out of direct radio range.
- **Topology**: Device A <--- BLE/Wi-Fi Direct ---> Device B (Relay) <--- BLE/Wi-Fi Direct ---> Device C.
- **Verification Criteria**:
  - Device B's logs must confirm packet forwarding of ciphertext.
  - Device B must NOT be able to decrypt the private payload (E2EE verified).
  - Device C sends back `DELIVERY_ACK`, which propagates back to Device A via Relay B.
