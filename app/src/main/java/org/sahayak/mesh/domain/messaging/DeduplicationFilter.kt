package org.sahayak.mesh.domain.messaging

import java.util.LinkedHashMap

class DeduplicationFilter(
    private val maxCapacity: Int = 10_000,
    private val cacheTtlMs: Long = 48 * 3600 * 1000L // 48 hours
) {
    private val seenCache = object : LinkedHashMap<String, Long>(maxCapacity, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Long>?): Boolean {
            return size > maxCapacity
        }
    }

    private val lock = Any()

    /**
     * Checks if packet has been seen. If not seen, records it and returns true (new).
     * If already seen and not expired, returns false (duplicate).
     */
    fun isNewPacket(packetId: String, currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        synchronized(lock) {
            val seenAt = seenCache[packetId]
            if (seenAt != null && (currentTimeMs - seenAt) < cacheTtlMs) {
                return false // Duplicate!
            }

            seenCache[packetId] = currentTimeMs
            return true
        }
    }

    /**
     * Inspects whether a packet was previously recorded without modifying cache.
     */
    fun hasSeen(packetId: String, currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        synchronized(lock) {
            val seenAt = seenCache[packetId] ?: return false
            return (currentTimeMs - seenAt) < cacheTtlMs
        }
    }

    fun clear() {
        synchronized(lock) {
            seenCache.clear()
        }
    }
}
