package org.sahayak.mesh.transport.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.sahayak.mesh.core.protocol.PacketEnvelope
import java.util.concurrent.CopyOnWriteArrayList

class SimulatedMeshTransport(
    val nodeId: String
) : MeshTransport {

    override val transportName: String = "SIMULATED_MESH"

    private val _connectedPeers = MutableStateFlow<List<PeerSession>>(emptyList())
    override val connectedPeers: Flow<List<PeerSession>> = _connectedPeers.asStateFlow()

    private val listeners = CopyOnWriteArrayList<PacketListener>()
    private val directNeighbors = mutableMapOf<String, SimulatedMeshTransport>()

    var isAdvertising = false
        private set
    var isDiscovering = false
        private set

    fun linkTo(other: SimulatedMeshTransport) {
        directNeighbors[other.nodeId] = other
        other.directNeighbors[this.nodeId] = this
        updatePeerList()
        other.updatePeerList()
    }

    fun unlinkFrom(other: SimulatedMeshTransport) {
        directNeighbors.remove(other.nodeId)
        other.directNeighbors.remove(this.nodeId)
        updatePeerList()
        other.updatePeerList()
    }

    private fun updatePeerList() {
        _connectedPeers.value = directNeighbors.keys.map { peerId ->
            PeerSession(
                peerId = peerId,
                endpointName = "Node-$peerId",
                isConnected = true,
                rssi = -55,
                transportType = "SIMULATED"
            )
        }
    }

    override suspend fun startAdvertising(localEndpointName: String) {
        isAdvertising = true
    }

    override suspend fun startDiscovery() {
        isDiscovering = true
    }

    override suspend fun stopAdvertising() {
        isAdvertising = false
    }

    override suspend fun stopDiscovery() {
        isDiscovering = false
    }

    override suspend fun sendPacket(peerId: String, envelope: PacketEnvelope): Boolean {
        val target = directNeighbors[peerId] ?: return false
        target.deliverPacket(envelope, fromPeerId = nodeId)
        return true
    }

    override suspend fun broadcastPacket(envelope: PacketEnvelope): Int {
        var count = 0
        directNeighbors.values.forEach { neighbor ->
            neighbor.deliverPacket(envelope, fromPeerId = nodeId)
            count++
        }
        return count
    }

    private fun deliverPacket(envelope: PacketEnvelope, fromPeerId: String) {
        listeners.forEach { it.onPacketReceived(envelope, fromPeerId) }
    }

    override fun registerPacketListener(listener: PacketListener) {
        listeners.add(listener)
    }

    override fun unregisterPacketListener(listener: PacketListener) {
        listeners.remove(listener)
    }
}
