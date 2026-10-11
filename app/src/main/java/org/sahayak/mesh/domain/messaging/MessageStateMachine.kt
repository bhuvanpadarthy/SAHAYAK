package org.sahayak.mesh.domain.messaging

import org.sahayak.mesh.core.model.Message
import org.sahayak.mesh.core.model.MessageState

class MessageStateMachine {

    /**
     * Attempts to transition the given message to [newState].
     * Returns the updated message if legal, or throws [IllegalStateException] if disallowed.
     */
    fun transition(message: Message, newState: MessageState): Message {
        val currentState = message.state

        if (currentState == newState) {
            return message // Idempotent no-op
        }

        if (!currentState.canTransitionTo(newState)) {
            throw IllegalStateException(
                "Illegal state transition for message ${message.id}: cannot transition from $currentState to $newState"
            )
        }

        return message.copy(state = newState)
    }

    /**
     * Checks if a message has exceeded its expiry time.
     */
    fun checkExpiry(message: Message, currentTimeMs: Long = System.currentTimeMillis()): Message {
        if (!message.state.isTerminal && message.expiresAtMs <= currentTimeMs) {
            return message.copy(state = MessageState.EXPIRED)
        }
        return message
    }
}
