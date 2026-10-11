package org.sahayak.mesh.domain.messaging

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeduplicationFilterTest {

    @Test
    fun testNewPacketIsAcceptedAndDuplicateIsDropped() {
        val filter = DeduplicationFilter(maxCapacity = 100, cacheTtlMs = 60_000L)
        val now = 1000_000L

        // First arrival is new
        assertTrue(filter.isNewPacket("pkt-alpha", now))

        // Second arrival of same packet is duplicate
        assertFalse(filter.isNewPacket("pkt-alpha", now + 1000L))

        // Different packet is new
        assertTrue(filter.isNewPacket("pkt-beta", now + 2000L))
    }

    @Test
    fun testExpiredPacketCanBeProcessedAgainAfterTTL() {
        val ttlMs = 5000L
        val filter = DeduplicationFilter(maxCapacity = 100, cacheTtlMs = ttlMs)
        val t0 = 10_000L

        assertTrue(filter.isNewPacket("pkt-gamma", t0))
        assertFalse(filter.isNewPacket("pkt-gamma", t0 + 1000L))

        // After TTL elapses
        assertTrue(filter.isNewPacket("pkt-gamma", t0 + ttlMs + 1L))
    }

    @Test
    fun testLruCapacityEviction() {
        val filter = DeduplicationFilter(maxCapacity = 2, cacheTtlMs = 100_000L)
        val now = 1000L

        filter.isNewPacket("pkt-1", now)
        filter.isNewPacket("pkt-2", now)
        // Adding 3rd pushes out eldest (pkt-1)
        filter.isNewPacket("pkt-3", now)

        // pkt-1 was evicted, so it is treated as new again if received
        assertTrue(filter.isNewPacket("pkt-1", now))
    }
}
