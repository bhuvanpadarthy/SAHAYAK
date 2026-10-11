# Sahayak-Mesh X — Wire & Message Protocol Specification (v1)

## 1. Protocol Goals

1. **Deterministic Serialization**: Envelopes and payloads must be structured, versioned, and easily validated across differing Android OS versions and architectures.
2. **Hop-by-Hop Routing with End-to-End Privacy**: Relays inspect only routing metadata (recipient hash, TTL, packet ID, priority), never private message payloads.
3. **Replay & Tamper Resistance**: All broadcasts and control packets are digitally signed. All one-to-one messages include per-message cryptographic nonces and authentication tags.
4. **Bounded Packet Size**: Control and text packets must fit within 64 KB to respect Bluetooth Low Energy L2CAP and Wi-Fi Direct buffer constraints.

---

## 2. Envelope Format

Every packet transmitted over the mesh is wrapped in a `PacketEnvelope`:

```json
{
  "protocolVersion": 1,
  "packetId": "550e8400-e29b-41d4-a716-446655440000",
  "senderId": "ed25519-public-key-hex-or-uuid",
  "recipientId": "ed25519-public-key-hex-or-broadcast",
  "missionId": "optional-mission-id-or-null",
  "payloadType": "CHAT_DIRECT | BROADCAST_ALERT | DELIVERY_ACK | SOS_ALERT | PEER_DISCOVERY",
  "priority": "LOW | NORMAL | HIGH | EMERGENCY",
  "hopLimit": 5,
  "hopCount": 0,
  "createdAtMs": 1791720981000,
  "expiresAtMs": 1791724581000,
  "signature": "base64-ed25519-signature-over-canonical-header-and-payload",
  "encryptedPayload": "base64-ciphertext-or-canonical-json-payload"
}
```

### Canonical Header for Signature Calculation:
```
canonical_bytes = UTF-8(protocolVersion + ":" + packetId + ":" + senderId + ":" + recipientId + ":" + payloadType + ":" + createdAtMs + ":" + expiresAtMs + ":" + SHA256(encryptedPayload))
```

---

## 3. Payload Types

### A. Direct Private Chat (`CHAT_DIRECT`)
- `encryptedPayload` contains the X25519 + ChaCha20-Poly1305 ciphertext.
- Decrypted plaintext schema:
```json
{
  "messageId": "msg-uuid",
  "conversationId": "peer-device-id",
  "textContent": "Hello from field team A",
  "timestampMs": 1791720981000,
  "replyToId": "optional-uuid"
}
```

### B. Mission Broadcast (`BROADCAST_ALERT`)
- Signed directly by an enrolled Coordinator.
- Payload schema (plaintext or group-encrypted):
```json
{
  "broadcastId": "bcast-uuid",
  "missionId": "mission-alpha",
  "coordinatorId": "coord-device-id",
  "targetRole": "ALL | RESPONDER | VOLUNTEER | STAFF | VISITOR",
  "targetZone": "BUILDING_C_FLOOR_2 | ZONE_NORTH",
  "alertLevel": "ADVISORY | WARNING | CRITICAL",
  "title": "Evacuate North Stairwell",
  "body": "Smoke detected near B2. Use Eastern emergency exits.",
  "sequenceNumber": 42,
  "supersedesBroadcastId": "optional-uuid-of-prior-announcement",
  "isCancellation": false,
  "requiresHumanAck": true
}
```

### C. Delivery Acknowledgment (`DELIVERY_ACK`)
- Emitted by recipient device or human responder:
```json
{
  "ackId": "ack-uuid",
  "originalPacketId": "msg-uuid-or-bcast-uuid",
  "originalSenderId": "original-sender-device-id",
  "recipientDeviceId": "my-device-id",
  "ackType": "RECEIVED_BY_DEVICE | READ_BY_RECIPIENT | HUMAN_ACKNOWLEDGED",
  "timestampMs": 1791721002000,
  "humanNotes": "Team is safe and moving to exit."
}
```

### D. Emergency SOS (`SOS_ALERT`)
- Highest priority (`EMERGENCY`), zero throttle, preempts normal queue:
```json
{
  "sosId": "sos-uuid",
  "originDeviceId": "device-uuid",
  "category": "MEDICAL | EVACUATION | ACCESSIBILITY | GENERAL",
  "severity": "CRITICAL",
  "latitude": 28.5450,
  "longitude": 77.1926,
  "locationDescription": "Main Quad, near library west gate",
  "batteryPercent": 68,
  "timestampMs": 1791721015000,
  "status": "OPEN | ASSIGNED | RESOLVED",
  "assignedResponderId": null
}
```

---

## 4. Relay Rules & Loop Prevention

1. **Deduplication**: Every relay node stores seen `packetId` in an LRU SQLite cache with a 48-hour TTL. Duplicate packets are silently dropped from forwarding.
2. **Hop Limit**: Packets arrive with `hopLimit` (default: 5) and `hopCount`. Upon receiving:
   - If `hopCount >= hopLimit`: Do NOT forward.
   - If packet `expiresAtMs < currentTimeMs`: Drop immediately.
   - When forwarding: increment `hopCount` by 1.
3. **Queue Prioritization**:
   - Priority 1: `EMERGENCY` (`SOS_ALERT`)
   - Priority 2: `CRITICAL` / `HIGH` (`BROADCAST_ALERT`)
   - Priority 3: `DELIVERY_ACK`
   - Priority 4: `NORMAL` (`CHAT_DIRECT`)
   - Priority 5: `LOW` (Heartbeats / Diagnostic telemetry)
