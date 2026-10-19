
package org.sahayak.mesh.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import org.sahayak.mesh.SahayakApp
import org.sahayak.mesh.core.model.PayloadType
import org.sahayak.mesh.core.model.Priority
import org.sahayak.mesh.core.protocol.PacketEnvelope
import org.sahayak.mesh.transport.api.NearbyConnectionsMeshTransport
import org.sahayak.mesh.transport.api.NearbyPermissions
import org.sahayak.mesh.transport.api.PacketListener
import java.nio.charset.StandardCharsets
import java.util.UUID

class MainActivity : ComponentActivity() {

    private val nearbyPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val denied = results.filterValues { !it }.keys

            val message = if (denied.isEmpty()) {
                "Nearby permissions granted"
            } else {
                "Nearby permissions are required for radio connectivity"
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as SahayakApp

        setContent {
            SahayakMeshTheme {
                MainScreen(
                    deviceId = app.deviceId,
                    transport = app.meshTransport
                )
            }
        }

        requestNearbyPermissionsIfNeeded()
    }

    private fun requestNearbyPermissionsIfNeeded() {
        val requiredPermissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.NEARBY_WIFI_DEVICES
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            else -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        val missing = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            nearbyPermissionLauncher.launch(missing.toTypedArray())
        }
    }
}

@Composable
fun SahayakMeshTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Color(0xFF38BDF8),
            onPrimary = Color(0xFF0F172A),
            surface = Color(0xFF0F172A),
            onSurface = Color(0xFFF8FAFC),
            background = Color(0xFF020617),
            onBackground = Color(0xFFF8FAFC),
            error = Color(0xFFEF4444),
            onError = Color.White
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Composable
fun MainScreen(
    deviceId: String,
    transport: NearbyConnectionsMeshTransport
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(Icons.Default.Hub, contentDescription = "Mesh")
                    },
                    label = { Text("Mesh Hub") }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(Icons.Default.Chat, contentDescription = "Chats")
                    },
                    label = { Text("Chats") }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(Icons.Default.Campaign, contentDescription = "Missions")
                    },
                    label = { Text("Missions") }
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(Icons.Default.Emergency, contentDescription = "SOS")
                    },
                    label = { Text("SOS") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SAHAYAK-MESH X",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Offline-First Emergency Coordination",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF94A3B8))
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "NEARBY STATUS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> MeshHubTab(
                    deviceId = deviceId,
                    transport = transport
                )

                1 -> ChatsTab(
                    deviceId = deviceId,
                    transport = transport
                )

                2 -> MissionsTab()
                3 -> SosEmergencyTab()
            }
        }
    }
}

@Composable
fun MeshHubTab(
    deviceId: String,
    transport: NearbyConnectionsMeshTransport
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val peers by transport.connectedPeers.collectAsState()

    var isRunning by remember { mutableStateOf(false) }
    var isStarting by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Not connected") }

    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Nearby Mesh Connection",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Status: $status",
                    color = Color(0xFF38BDF8),
                    fontSize = 13.sp
                )

                Text(
                    text = "Connected devices: ${peers.count { it.isConnected }}",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        scope.launch {
                            if (!NearbyPermissions.hasPermissions(context)) {
                                status = "Nearby permissions required"
                                Toast.makeText(
                                    context,
                                    "Grant Nearby permissions in app settings.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@launch
                            }

                            isStarting = true
                            status = "Starting nearby connection..."

                            try {
                                transport.startAdvertising(Build.MODEL)
                                transport.startDiscovery()

                                isRunning = true
                                status = "Advertising and discovering"
                            } catch (e: Exception) {
                                status = "Could not start: ${
                                    e.localizedMessage ?: "Unknown error"
                                }"
                            } finally {
                                isStarting = false
                            }
                        }
                    },
                    enabled = !isRunning && !isStarting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isStarting) "Starting..." else "Start Nearby")
                }

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            try {
                                transport.stopDiscovery()
                                transport.stopAdvertising()

                                isRunning = false
                                status = "Nearby stopped"
                            } catch (e: Exception) {
                                status = "Stop failed: ${
                                    e.localizedMessage ?: "Unknown error"
                                }"
                            }
                        }
                    },
                    enabled = isRunning && !isStarting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Stop Nearby")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = "Identity",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Device Identity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ID: ${deviceId.take(16)}...${deviceId.takeLast(8)}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color(0xFF38BDF8)
                )

                Text(
                    text = "Cryptographic identity initialized",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Store-and-Forward Engine Status",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MetricRow("Local Outbox Queue", "0 pending")
                MetricRow("Store & Forward Cache", "0 relayed")
                MetricRow(
                    "Direct Radio Peers",
                    "${peers.count { it.isConnected }} connected"
                )
                MetricRow("Max Relay Hop Limit", "5 hops")
            }
        }

        peers.forEach { peer ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nearby device: ${peer.endpointName}",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = Color(0xFFF1F5F9),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

private data class ChatMessageUi(
    val packetId: String,
    val senderId: String,
    val text: String,
    val isMine: Boolean
)

@Composable
fun ChatsTab(
    deviceId: String,
    transport: NearbyConnectionsMeshTransport
) {
    val peers by transport.connectedPeers.collectAsState()
    val messages = remember { mutableStateListOf<ChatMessageUi>() }
    val scope = rememberCoroutineScope()

    var draft by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Ready") }

    DisposableEffect(transport, deviceId) {
        val listener = object : PacketListener {
            override fun onPacketReceived(
                envelope: PacketEnvelope,
                fromPeerId: String
            ) {
                if (envelope.payloadType != PayloadType.CHAT_DIRECT) return
                if (envelope.senderId == deviceId) return
                if (envelope.expiresAtMs <= System.currentTimeMillis()) return

                val decodedText = try {
                    String(
                        Base64.decode(
                            envelope.encryptedPayload,
                            Base64.DEFAULT
                        ),
                        StandardCharsets.UTF_8
                    )
                } catch (_: IllegalArgumentException) {
                    return
                }

                scope.launch {
                    if (messages.none { it.packetId == envelope.packetId }) {
                        messages.add(
                            ChatMessageUi(
                                packetId = envelope.packetId,
                                senderId = envelope.senderId,
                                text = decodedText,
                                isMine = false
                            )
                        )
                        status = "Message received"
                    }
                }
            }
        }

        transport.registerPacketListener(listener)

        onDispose {
            transport.unregisterPacketListener(listener)
        }
    }

    val connectedPeer = peers.firstOrNull { it.isConnected }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Mesh Chat — Test Version",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (connectedPeer != null) {
                "Connected to: ${connectedPeer.endpointName}"
            } else {
                "No connected peer. Start Nearby on Mesh Hub."
            },
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Text(
            text = status,
            fontSize = 12.sp,
            color = Color(0xFF38BDF8)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            )
        ) {
            Text(
                text = "Test mode: messages are not end-to-end encrypted.",
                modifier = Modifier.padding(12.dp),
                fontSize = 12.sp,
                color = Color(0xFFFBBF24)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.packetId }) { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.isMine) {
                            Color(0xFF164E63)
                        } else {
                            Color(0xFF1E293B)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (message.isMine) {
                                "You"
                            } else {
                                "Peer ${message.senderId.take(12)}"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = message.text,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Type a test message") },
            enabled = connectedPeer != null,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val peer = connectedPeer
                val textToSend = draft.trim()

                if (peer == null) {
                    status = "No connected peer"
                    return@Button
                }

                if (textToSend.isEmpty()) {
                    status = "Type a message first"
                    return@Button
                }

                scope.launch {
                    status = "Submitting message..."

                    try {
                        val encodedText = Base64.encodeToString(
                            textToSend.toByteArray(StandardCharsets.UTF_8),
                            Base64.NO_WRAP
                        )

                        val envelope = PacketEnvelope(
                            packetId = UUID.randomUUID().toString(),
                            senderId = deviceId,
                            recipientId = peer.peerId,
                            payloadType = PayloadType.CHAT_DIRECT,
                            priority = Priority.NORMAL,
                            encryptedPayload = encodedText
                        )

                        val accepted = transport.sendPacket(
                            peer.peerId,
                            envelope
                        )

                        if (accepted) {
                            messages.add(
                                ChatMessageUi(
                                    packetId = envelope.packetId,
                                    senderId = deviceId,
                                    text = textToSend,
                                    isMine = true
                                )
                            )
                            draft = ""
                            status = "Submitted to Nearby; receipt not confirmed"
                        } else {
                            status = "Nearby did not accept the message"
                        }
                    } catch (e: Exception) {
                        status = "Send failed: ${
                            e.localizedMessage ?: "Unknown error"
                        }"
                    }
                }
            },
            enabled = connectedPeer != null && draft.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Chat,
                contentDescription = "Send message"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Test Message")
        }
    }
}

@Composable
fun MissionsTab() {
    Column {
        Text(
            text = "Mission Broadcasts & Bulletins",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Authenticated mission broadcasts are not implemented yet.",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No active mission.",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun SosEmergencyTab() {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Emergency Assistance Request",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFFEF4444)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "The SOS broadcast action is a placeholder and does not send an alert yet.",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                Toast.makeText(
                    context,
                    "SOS broadcasting is not implemented yet.",
                    Toast.LENGTH_LONG
                ).show()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDC2626)
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                Icons.Default.Emergency,
                contentDescription = "SOS"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "BROADCAST MEDICAL SOS",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
