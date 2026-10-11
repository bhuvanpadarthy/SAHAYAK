package org.sahayak.mesh.core.model

/**
 * Explicit states for messages in the Sahayak-Mesh X store-and-forward engine.
 *
 * In accordance with our verification rules:
 * - A message is NEVER marked as delivered merely because a relay accepted it.
 * - Delivery is per-recipient.
 * - Transitions are strictly validated.
 */
enum class MessageState {
    /**
     * Stored in local durable Outbox, awaiting radio transmission to a peer.
     */
    LOCAL_QUEUED,

    /**
     * Successfully transmitted over local radio (Bluetooth/Wi-Fi Direct) to at least one peer.
     * Note: This does NOT mean the final recipient received it.
     */
    TRANSMITTED,

    /**
     * A verified, cryptographically signed delivery ACK was received from the recipient's device.
     */
    RECEIVED_BY_DEVICE,

    /**
     * The recipient user opened the conversation or notification containing this message.
     */
    READ_BY_RECIPIENT,

    /**
     * A human participant explicitly tapped an acknowledgment button (e.g. "Understood", "Moving").
     */
    HUMAN_ACKNOWLEDGED,

    /**
     * Terminal failure state (e.g. unrecoverable serialization failure, bad signature).
     */
    FAILED,

    /**
     * Terminal expired state (TTL elapsed before delivery could be confirmed).
     */
    EXPIRED,

    /**
     * Cancelled by sender or coordinator before transmission or fulfillment.
     */
    CANCELLED;

    /**
     * Checks if this state represents a terminal condition from which no further normal progression occurs.
     */
    val isTerminal: Boolean
        get() = this == FAILED || this == EXPIRED || this == CANCELLED

    /**
     * Validates whether transition from `this` to [newState] is legal.
     */
    fun canTransitionTo(newState: MessageState): Boolean {
        if (this == newState) return true
        if (this.isTerminal) return false

        return when (this) {
            LOCAL_QUEUED -> newState in setOf(TRANSMITTED, RECEIVED_BY_DEVICE, FAILED, EXPIRED, CANCELLED)
            TRANSMITTED -> newState in setOf(RECEIVED_BY_DEVICE, READ_BY_RECIPIENT, HUMAN_ACKNOWLEDGED, FAILED, EXPIRED, CANCELLED)
            RECEIVED_BY_DEVICE -> newState in setOf(READ_BY_RECIPIENT, HUMAN_ACKNOWLEDGED, EXPIRED)
            READ_BY_RECIPIENT -> newState in setOf(HUMAN_ACKNOWLEDGED, EXPIRED)
            HUMAN_ACKNOWLEDGED -> false
            FAILED, EXPIRED, CANCELLED -> false
        }
    }
}
