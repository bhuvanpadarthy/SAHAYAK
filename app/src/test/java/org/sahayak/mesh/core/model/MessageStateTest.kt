package org.sahayak.mesh.core.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sahayak.mesh.domain.messaging.MessageStateMachine

class MessageStateTest {

    private val stateMachine = MessageStateMachine()

    @Test
    fun testValidStateProgression() {
        val baseMessage = Message(
            id = "msg-1",
            conversationId = "conv-1",
            senderId = "sender-1",
            recipientId = "recip-1",
            content = "Testing mesh delivery",
            state = MessageState.LOCAL_QUEUED
        )

        // LOCAL_QUEUED -> TRANSMITTED
        val transmitted = stateMachine.transition(baseMessage, MessageState.TRANSMITTED)
        assertTrue(transmitted.state == MessageState.TRANSMITTED)

        // TRANSMITTED -> RECEIVED_BY_DEVICE
        val received = stateMachine.transition(transmitted, MessageState.RECEIVED_BY_DEVICE)
        assertTrue(received.state == MessageState.RECEIVED_BY_DEVICE)

        // RECEIVED_BY_DEVICE -> READ_BY_RECIPIENT
        val read = stateMachine.transition(received, MessageState.READ_BY_RECIPIENT)
        assertTrue(read.state == MessageState.READ_BY_RECIPIENT)

        // READ_BY_RECIPIENT -> HUMAN_ACKNOWLEDGED
        val acked = stateMachine.transition(read, MessageState.HUMAN_ACKNOWLEDGED)
        assertTrue(acked.state == MessageState.HUMAN_ACKNOWLEDGED)
    }

    @Test(expected = IllegalStateException::class)
    fun testDisallowedTransitionFromTerminalState() {
        val failedMessage = Message(
            id = "msg-2",
            conversationId = "conv-1",
            senderId = "sender-1",
            recipientId = "recip-1",
            content = "Dead message",
            state = MessageState.FAILED
        )

        // Must throw IllegalStateException
        stateMachine.transition(failedMessage, MessageState.TRANSMITTED)
    }

    @Test(expected = IllegalStateException::class)
    fun testDisallowedBackwardsTransition() {
        val readMessage = Message(
            id = "msg-3",
            conversationId = "conv-1",
            senderId = "sender-1",
            recipientId = "recip-1",
            content = "Already read",
            state = MessageState.READ_BY_RECIPIENT
        )

        // Cannot jump back to LOCAL_QUEUED
        stateMachine.transition(readMessage, MessageState.LOCAL_QUEUED)
    }

    @Test
    fun testTerminalStatesAreRecognized() {
        assertTrue(MessageState.FAILED.isTerminal)
        assertTrue(MessageState.EXPIRED.isTerminal)
        assertTrue(MessageState.CANCELLED.isTerminal)
        assertFalse(MessageState.LOCAL_QUEUED.isTerminal)
        assertFalse(MessageState.TRANSMITTED.isTerminal)
    }

    @Test
    fun testIdempotentSameStateTransition() {
        val message = Message(
            id = "msg-4",
            conversationId = "conv-1",
            senderId = "sender-1",
            recipientId = "recip-1",
            content = "Noop",
            state = MessageState.TRANSMITTED
        )

        val result = stateMachine.transition(message, MessageState.TRANSMITTED)
        assertTrue(result.state == MessageState.TRANSMITTED)
    }
}
