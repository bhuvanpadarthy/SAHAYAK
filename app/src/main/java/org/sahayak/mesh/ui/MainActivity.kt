package org.sahayak.mesh.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sahayak.mesh.SahayakApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as SahayakApp

        setContent {
            SahayakMeshTheme {
                MainScreen(
                    deviceId = app.deviceId
                )
            }
        }
    }
}

@Composable
fun SahayakMeshTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Color(0xFF38BDF8), // Electric cyan
            onPrimary = Color(0xFF0F172A),
            surface = Color(0xFF0F172A), // Slate 900
            onSurface = Color(0xFFF8FAFC),
            background = Color(0xFF020617), // Slate 950
            onBackground = Color(0xFFF8FAFC),
            error = Color(0xFFEF4444), // Crimson SOS
            onError = Color(0xFFFFFFFF)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

@Composable
fun MainScreen(deviceId: String) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Hub, contentDescription = "Mesh") },
                    label = { Text("Mesh Hub") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
                    label = { Text("Chats") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = "Missions") },
                    label = { Text("Missions") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Emergency, contentDescription = "SOS") },
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
            // Header Bar
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

                // Radio status badge
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
                            .background(Color(0xFF22C55E))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MESH READY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> MeshHubTab(deviceId = deviceId)
                1 -> ChatsTab()
                2 -> MissionsTab()
                3 -> SosEmergencyTab()
            }
        }
    }
}

@Composable
fun MeshHubTab(deviceId: String) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
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
                        text = "Device Identity (Hardware Key)",
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
                    text = "Ed25519 Identity & X25519 E2EE Active",
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MetricRow(label = "Local Outbox Queue", value = "0 pending")
                MetricRow(label = "Store & Forward Cache", value = "0 relayed")
                MetricRow(label = "Direct Radio Peers", value = "0 discovered")
                MetricRow(label = "Max Relay Hop Limit", value = "5 hops")
            }
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
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 13.sp)
        Text(text = value, color = Color(0xFFF1F5F9), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
fun ChatsTab() {
    Column {
        Text(
            text = "Private Encrypted Chats",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "End-to-end encrypted with X25519. Intermediate relays cannot decrypt plaintext.",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No active conversations. Scan nearby QR code to exchange verified identity keys.",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
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
            text = "Broadcasts require digital signatures from authenticated Coordinators.",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No active mission enrolled. Waiting for signed coordinator announcement beacon.",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun SosEmergencyTab() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Emergency Assistance Request",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFFEF4444)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Broadcasts highest priority SOS alert across all mesh hops without throttling.",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* SOS trigger */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Default.Emergency, contentDescription = "Trigger SOS")
            Spacer(modifier = Modifier.width(8.dp))
            Text("BROADCAST MEDICAL SOS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
