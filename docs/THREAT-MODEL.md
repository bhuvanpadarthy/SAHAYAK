# Sahayak-Mesh X — Security Architecture & Threat Model

## 1. System Assumptions and Trust Boundaries

### A. Environment Assumptions
1. **Adversarial / Untrusted Relay Nodes**: Devices participating in the store-and-forward mesh may be compromised, malicious, or curious. They MUST NEVER be able to read one-to-one message contents or tamper with broadcast instructions undetected.
2. **Untrusted Radio Medium**: Nearby radio signals (Bluetooth, Wi-Fi Direct) are subject to eavesdropping, packet injection, jamming, and replay attacks.
3. **Hardware Storage Vulnerability**: A lost or stolen physical phone must not compromise other network participants or expose long-term private keys if hardware keystore protection is active.
4. **Clock Skew**: In offline disaster scenarios, devices may lose NTP synchronization. All timestamps, TTLs, and sequence numbers must tolerate bounded clock skew (+/- 15 minutes) with monotonic sequence checks.

---

## 2. Threat Vectors and Mitigations

| Threat | Description | Mitigation in Sahayak-Mesh X |
| :--- | :--- | :--- |
| **Impersonated Coordinator** | Malicious actor broadcasts fake evacuation or panic notices. | Every broadcast requires a cryptographic signature (Ed25519) verified against pre-enrolled coordinator public keys stored in local Room DB. Unsigned/unverified broadcasts are rejected by the domain layer. |
| **Unauthorized Mission Membership** | An unauthorized device attempts to decrypt mission channels or join incident dispatch. | Mission tickets are cryptographically signed during in-person QR enrollment or cryptographic handshake. RBAC permissions enforced at repository boundary. |
| **Packet Forgery & Tampering** | An intermediate relay modifies coordinates, text, or alert urgency in transit. | Signatures cover canonicalized envelope headers and payloads. AEAD (ChaCha20-Poly1305 / AES-GCM) provides tamper detection for private messages; invalid auth tags result in instant drop. |
| **Replay Attacks** | An attacker captures a valid evacuation or SOS packet and re-transmits it hours later. | Nonces, creation timestamps with expiry window, and an LRU packet deduplication table of seen UUIDs prevent reprocessing. |
| **Compromised Relays & Plaintext Leakage** | Intermediate mesh hops inspect private citizen messages. | End-to-end encryption (E2EE) with X25519 Diffie-Hellman and per-session keys. Relays only inspect outer routing envelope. |
| **Denial of Service / Buffer Exhaustion** | An attacker floods the mesh with gigantic payloads or infinite message loops. | Strict packet size bounds (max 64KB for control/chat), hop limits (max 5 hops), strict queue caps (max 500 queued packets), and priority-based LRU cache eviction (Emergency SOS given top retention priority). |
| **Sybil & Identity Spam** | A rogue device generates hundreds of fake identities to spam channels. | Local contact blocking, trust state tagging (`VERIFIED_IN_PERSON` vs `UNKNOWN`), and rate-limiting per peer session. |
| **Revoked Coordinator / Compromised Keys** | A coordinator's phone is stolen. | Revocation packets signed by the Master Authority or a quorum of coordinators propagate through the mesh with high priority. Revoked keys immediately invalidate future and pending messages. |

---

## 3. Cryptographic Primitives

- **Digital Signatures**: Ed25519 (RFC 8032) for device identity, mission invitations, and broadcast authorizations.
- **Key Exchange**: X25519 (RFC 7748) for Diffie-Hellman ephemeral/static key agreement.
- **Authenticated Encryption**: ChaCha20-Poly1305 (RFC 8439) / AES-256-GCM.
- **Key Storage**: Keys generated and stored using Android Keystore (`AndroidKeyStore`) with StrongBox or TEE where supported by the device.
- **Randomness**: `java.security.SecureRandom`.

---

## 4. Privacy & Data Minimization

- **No Plaintext Identifiers**: Peer greetings do not transmit personal names or phone numbers in cleartext over unencrypted beacons.
- **Metadata Protection**: Envelope routing identifiers use truncated key hashes or ephemeral session tokens where feasible.
- **Local Data Retention**: Room database stores messages with configurable retention policies (e.g., auto-purge after 7 days post-mission).
- **Log Sanitization**: ProGuard / R8 rules and custom Timber/Logger tree ensure zero plaintext, tokens, or private keys are written to Logcat.
