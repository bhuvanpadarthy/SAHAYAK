
package org.sahayak.mesh.transport.api

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import org.sahayak.mesh.core.protocol.PacketEnvelope
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class NearbyConnectionsMeshTransport(
    context: Context,
    private val serviceId: String,
    private val localNodeId: String
) : MeshTransport {

    private val appContext = context.applicationContext

    private val client: ConnectionsClient =
        Nearby.getConnectionsClient(appContext)

    private val gson = Gson()

    override val transportName = "Google Nearby Connections"

    private val _connectedPeers =
        MutableStateFlow<List<PeerSession>>(emptyList())

    override val connectedPeers: StateFlow<List<PeerSession>> =
        _connectedPeers.asStateFlow()

    private val endpointToPeerId =
        ConcurrentHashMap<String, String>()

    private val peerIdToEndpoint =
        ConcurrentHashMap<String, String>()

    private val endpointNames =
        ConcurrentHashMap<String, String>()

    private val packetListeners =
        CopyOnWriteArraySet<PacketListener>()

    @Volatile
    private var localEndpointName = localNodeId

    private val payloadCallback = object : PayloadCallback() {

        override fun onPayloadReceived(
            endpointId: String,
            payload: Payload
        ) {
            if (payload.type != Payload.Type.BYTES) {
                Log.w(TAG, "Ignoring non-byte payload from $endpointId")
                return
            }

            val bytes = payload.asBytes() ?: run {
                Log.w(TAG, "Empty payload from $endpointId")
                return
            }

            if (bytes.size > PacketEnvelope.MAX_PACKET_BYTES) {
                Log.w(TAG, "Packet exceeds size limit")
                return
            }

            try {
                val json = String(bytes, StandardCharsets.UTF_8)

                val envelope = gson.fromJson(
                    json,
                    PacketEnvelope::class.java
                ) ?: run {
                    Log.w(TAG, "Invalid packet received")
                    return
                }

                if (envelope.protocolVersion !=
                    PacketEnvelope.CURRENT_VERSION
                ) {
                    Log.w(TAG, "Unsupported packet version")
                    return
                }

                val fromPeerId =
                    endpointToPeerId[endpointId] ?: endpointId

                packetListeners.forEach { listener ->
                    try {
                        listener.onPacketReceived(
                            envelope,
                            fromPeerId
                        )
                    } catch (e: Exception) {
                        Log.e(
                            TAG,
                            "Packet listener failed",
                            e
                        )
                    }
                }

                Log.d(
                    TAG,
                    "Packet received from $fromPeerId"
                )
            } catch (e: Exception) {
                Log.e(TAG, "Could not decode received packet", e)
            }
        }

        override fun onPayloadTransferUpdate(
            endpointId: String,
            update: PayloadTransferUpdate
        ) {
            if (update.status ==
                PayloadTransferUpdate.Status.FAILURE
            ) {
                Log.e(
                    TAG,
                    "Payload transfer failed for $endpointId"
                )
            }
        }
    }

    private val connectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {

            override fun onConnectionInitiated(
                endpointId: String,
                connectionInfo: ConnectionInfo
            ) {
                endpointNames[endpointId] =
                    connectionInfo.endpointName

                client.acceptConnection(
                    endpointId,
                    payloadCallback
                ).addOnFailureListener { error ->
                    Log.e(
                        TAG,
                        "Could not accept connection",
                        error
                    )
                }
            }

            override fun onConnectionResult(
                endpointId: String,
                result: ConnectionResolution
            ) {
                if (result.status.isSuccess) {
                    endpointToPeerId[endpointId] = endpointId
                    peerIdToEndpoint[endpointId] = endpointId

                    val name =
                        endpointNames[endpointId] ?: endpointId

                    _connectedPeers.value =
                        _connectedPeers.value
                            .filterNot { it.peerId == endpointId } +
                            PeerSession(
                                peerId = endpointId,
                                endpointName = name,
                                isConnected = true
                            )

                    Log.d(TAG, "Connected to $name")
                } else {
                    removeEndpoint(endpointId)

                    Log.w(
                        TAG,
                        "Connection failed: " +
                            result.status.statusCode
                    )
                }
            }

            override fun onDisconnected(endpointId: String) {
                Log.d(TAG, "Disconnected: $endpointId")
                removeEndpoint(endpointId)
            }
        }

    private val endpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {

            override fun onEndpointFound(
                endpointId: String,
                info: DiscoveredEndpointInfo
            ) {
                endpointNames[endpointId] = info.endpointName

                // Avoid requesting the same connection repeatedly.
                if (peerIdToEndpoint.containsValue(endpointId)) {
                    return
                }

                client.requestConnection(
                    localEndpointName,
                    endpointId,
                    connectionLifecycleCallback
                ).addOnFailureListener { error ->
                    Log.e(
                        TAG,
                        "Could not request connection",
                        error
                    )
                }
            }

            override fun onEndpointLost(endpointId: String) {
                Log.d(TAG, "Endpoint lost: $endpointId")
            }
        }

    override suspend fun startAdvertising(
        localEndpointName: String
    ) {
        check(NearbyPermissions.hasPermissions(appContext)) {
            "Nearby permissions are missing."
        }

        this.localEndpointName = localEndpointName

        client.startAdvertising(
            localEndpointName,
            serviceId,
            connectionLifecycleCallback,
            AdvertisingOptions.Builder()
                .setStrategy(Strategy.P2P_CLUSTER)
                .build()
        ).await()

        Log.d(TAG, "Advertising started")
    }

    override suspend fun startDiscovery() {
        check(NearbyPermissions.hasPermissions(appContext)) {
            "Nearby permissions are missing."
        }

        client.startDiscovery(
            serviceId,
            endpointDiscoveryCallback,
            DiscoveryOptions.Builder()
                .setStrategy(Strategy.P2P_CLUSTER)
                .build()
        ).await()

        Log.d(TAG, "Discovery started")
    }

    override suspend fun stopAdvertising() {
        client.stopAdvertising()
        Log.d(TAG, "Advertising stopped")
    }

    override suspend fun stopDiscovery() {
        client.stopDiscovery()
        Log.d(TAG, "Discovery stopped")
    }

    override suspend fun sendPacket(
        peerId: String,
        envelope: PacketEnvelope
    ): Boolean {
        val endpointId = peerIdToEndpoint[peerId]
            ?: return false

        return try {
            val bytes = gson.toJson(envelope)
                .toByteArray(StandardCharsets.UTF_8)

            require(bytes.size <= PacketEnvelope.MAX_PACKET_BYTES) {
                "Packet exceeds the maximum allowed size."
            }

            client.sendPayload(
                endpointId,
                Payload.fromBytes(bytes)
            ).await()

            Log.d(TAG, "Packet sent to $peerId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Could not send packet to $peerId", e)
            false
        }
    }

    override suspend fun broadcastPacket(
        envelope: PacketEnvelope
    ): Int {
        val bytes = gson.toJson(envelope)
            .toByteArray(StandardCharsets.UTF_8)

        require(bytes.size <= PacketEnvelope.MAX_PACKET_BYTES) {
            "Packet exceeds the maximum allowed size."
        }

        val endpoints = peerIdToEndpoint.values
            .distinct()
            .toList()

        if (endpoints.isEmpty()) return 0

        return try {
            client.sendPayload(
                endpoints,
                Payload.fromBytes(bytes)
            ).await()

            Log.d(
                TAG,
                "Broadcast submitted to ${endpoints.size} peers"
            )

            endpoints.size
        } catch (e: Exception) {
            Log.e(TAG, "Broadcast failed", e)
            0
        }
    }

    override fun registerPacketListener(
        listener: PacketListener
    ) {
        packetListeners.add(listener)
    }

    override fun unregisterPacketListener(
        listener: PacketListener
    ) {
        packetListeners.remove(listener)
    }

    private fun removeEndpoint(endpointId: String) {
        val peerId =
            endpointToPeerId.remove(endpointId) ?: endpointId

        peerIdToEndpoint.remove(peerId)
        endpointNames.remove(endpointId)

        _connectedPeers.value =
            _connectedPeers.value.filterNot {
                it.peerId == peerId
            }
    }

    private companion object {
        const val TAG = "NearbyMesh"
    }
}
