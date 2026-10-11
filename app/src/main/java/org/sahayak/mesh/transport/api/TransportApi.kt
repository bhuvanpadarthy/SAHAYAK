package org.sahayak.mesh.transport.api

import kotlinx.coroutines.flow.Flow
import org.sahayak.mesh.core.protocol.PacketEnvelope

data class PeerSession(
    val peerId: String,
    val endpointName: String,
    val isConnected: Boolean,
    val rssi: Int? = null,
    val transportType: String = "NEARBY_P2P"
)

interface PacketListener {
    fun onPacketReceived(envelope: PacketEnvelope, fromPeerId: String)
}

interface MeshTransport {
    val transportName: String
    val connectedPeers: Flow<List<PeerSession>>

    suspend fun startAdvertising(localEndpointName: String)
    suspend fun startDiscovery()
    suspend fun stopAdvertising()
    suspend fun stopDiscovery()

    /**
     * Transmits a packet envelope to a specific peer endpoint.
     * Returns true if transmitted across local radio link, false otherwise.
     */
    suspend fun sendPacket(peerId: String, envelope: PacketEnvelope): Boolean

    /**
     * Broadcasts a packet envelope to all currently connected nearby peers.
     */
    suspend fun broadcastPacket(envelope: PacketEnvelope): Int

    fun registerPacketListener(listener: PacketListener)
    fun unregisterPacketListener(listener: PacketListener)
}
