# Sahayak-Mesh X — Security Architecture & Threat Model

## 1. System Assumptions and Trust Boundaries

### A. Environment Assumptions
1. **Adversarial / Untrusted Relay Nodes**: Devices participating in the store-and-forward mesh may be compromised, malicious, or curious. They MUST NEVER be able to read one-to-one message contents or tamper with broadcast instructions undetected.
2. **Untrusted Radio Medium**: Nearby radio signals (Bluetooth, Wi-Fi Direct) are subject to eavesdropping, packet injection, jamming, and replay attacks.
3. **Hardware Storage Vulnerability**: A lost or stolen physical phone must not compromise other network participants or expose long-term private keys if hardware keystore protection is active.
4. **Clock Skew**: In offline disaster scenarios, devices may lose NTP synchronization. All timestamps, TTLs, and sequence numbers must tolerate bounded clock skew (+/- 15 minutes) with monotonic sequence checks.

---

## 2. Cryptographic Architecture & Review Findings (Focused Codebase Audit)

### A. Current Implementation Status & Evaluation
- **Digital Signatures**: Ed25519 (RFC 8032) via Bouncy Castle. Verified for coordinator broadcasts and identity signatures.
- **Key Agreement**: X25519 (RFC 7748) Ephemeral-Static Diffie-Hellman with zero-shared-secret validation.
- **Key Derivation**: HKDF-SHA256 (RFC 5869) with domain separation tag (`Sahayak-Mesh-X-v1-E2EE`) and ephemeral public key as salt.
- **Symmetric Cipher**: AES-256-GCM AEAD with 12-byte random IV and 128-bit authentication tag.
- **Associated Authenticated Data (AAD)**: Binds sender and recipient public keys into AES-GCM to prevent Unknown Key Share (UKS) and sender spoofing.

### B. Unresolved Cryptographic Risks & Limitations (CRITICAL)
1. **Ad-Hoc / Custom Hybrid Assembly (AGENTS.md Rule 5)**:
   - Current one-to-one encryption is an ad-hoc Ephemeral-Static hybrid scheme (ECIES-like), assembled manually from cryptographic primitives.
   - While Bouncy Castle implements the primitives, assembling custom envelopes and handshakes violates the principle of using vetted, end-to-end security protocols.
2. **Asymmetric Forward Secrecy Limitation (No Double Ratchet)**:
   - The scheme provides forward secrecy *only* against future compromise of the sender's static key.
   - If the *recipient's static private key* is compromised, an adversary who intercepted and stored historical mesh traffic can compute $X25519(sk_{recipient}, ephem\_pk)$ for all past messages and decrypt them.
   - There is NO bidirectional Diffie-Hellman ratcheting and NO Post-Compromise Security (Break-in recovery).
3. **Replay Window Vulnerability across Device Restarts**:
   - `DeduplicationFilter` operates as an in-memory LRU cache.
   - If an Android device process is killed or the phone is rebooted, the deduplication cache is reset, allowing past valid packets to be replayed by an attacker until the cache is repopulated.
4. **Transport Implementation Status**:
   - Google Nearby Connections radio implementation is currently **0% implemented** (only the `MeshTransport` interface and JVM `SimulatedMeshTransport` exist in code).

### C. Recommended Standard Protocol Migration (Phase 2 Roadmap)
To satisfy the "No Custom Cryptography" golden rule and achieve true double-ratcheted forward secrecy and post-compromise security, the project must migrate to one of the following reviewed protocols:
1. **Google Tink HPKE (Hybrid Public Key Encryption - RFC 9180)**:
   - Standardized, reviewed, and audited implementation available via `com.google.crypto.tink:tink-android`.
   - Native support for Auth-Encap (authenticated sender mode) with explicit Associated Data (`aad`) binding.
2. **Noise Protocol Framework (e.g. `Noise_IK` or `Noise_XX` via `flynn/noise-java`)**:
   - Formally verified peer-to-peer / mesh secure messaging framework providing mutual authentication, zero-round-trip encryption, and channel binding.
3. **Signal Protocol (Double Ratchet + X3DH)**:
   - Industry standard for asynchronous offline messaging with full forward secrecy and break-in recovery.

---

## 3. Threat Vectors and Current Mitigation Status

| Threat | Description | Current Mitigation in Codebase | Remaining Residual Risk |
| :--- | :--- | :--- | :--- |
| **Impersonated Coordinator** | Malicious actor broadcasts fake evacuation or panic notices. | `MissionAuthorizer` enforces Ed25519 digital signature validation against coordinator public key. Also verifies active status, expiration time, and `BROADCAST_ALERT` payload type. | Coordinator private key theft on device without StrongBox Keystore. |
| **Oversized / DoS Packets** | Flooding mesh with gigantic payloads to exhaust memory. | `ProtocolValidator` enforces strict 64 KB maximum packet size bounds and hop limit checks. | Radio-level jamming cannot be mitigated at application layer. |
| **Inverted Lifetime Packets** | Packets with corrupt or malicious timestamps crafted to bypass expiration. | `ProtocolValidator` rejects packets where `expiresAtMs <= createdAtMs`. | None at validation layer. |
| **Delivered Message Expiry Glitch** | Previously delivered or read messages falsely transitioning to expired. | `MessageStateMachine` restricts expiry strictly to `LOCAL_QUEUED` and `TRANSMITTED` states. | None at domain state machine layer. |
| **Relay Eavesdropping** | Intermediate mesh hops inspect citizen private messages. | X25519 + HKDF-SHA256 + AES-256-GCM AEAD encryption. Verified in `SimulatedMeshRoutingTest`. | Recipient long-term key compromise decrypts historical traffic (lacks double ratchet). |
| **Unknown Key Share / Impostor AAD** | Attacker substitutes sender ID on encrypted ciphertext. | AES-GCM Associated Data (`aad`) binds sender and recipient public keys into AEAD tag. | Verified in `CryptoEngineTest`. |
| **Process Termination Data Loss** | Unsent messages lost when Android OS kills background process. | Room SQLite database persists outbox messages. Verified in `RoomPersistenceTest`. | Database file unencrypted if device storage is unlocked without Android Keystore SQLCipher. |
| **Replay Attacks across Process Death** | Attacker replays captured valid packets after device reboots. | `DeduplicationFilter` has 48h TTL in-memory. | **HIGH**: In-memory cache is cleared on process death. Must be backed by Room SQLite table. |
