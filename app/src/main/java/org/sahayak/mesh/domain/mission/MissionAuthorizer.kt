package org.sahayak.mesh.domain.mission

import org.sahayak.mesh.core.model.Mission
import org.sahayak.mesh.core.model.Role
import org.sahayak.mesh.core.protocol.PacketEnvelope
import org.sahayak.mesh.core.security.CryptoEngine
import java.util.Base64

class MissionAuthorizer {

    /**
     * Verifies that the sender is authorized to issue a broadcast for the specified mission
     * and that the packet contains a valid Ed25519 digital signature matching the coordinator key.
     */
    fun authorizeBroadcast(envelope: PacketEnvelope, mission: Mission): Boolean {
        // Must match mission ID
        if (envelope.missionId != mission.missionId) {
            return false
        }

        // Mission must be active
        if (!mission.isActive) {
            return false
        }

        // Sender must be the coordinator
        if (envelope.senderId != mission.coordinatorId) {
            return false
        }

        // Must have signature
        val signatureBase64 = envelope.signature ?: return false

        return try {
            val signatureBytes = Base64.getDecoder().decode(signatureBase64)
            val pubKeyBytes = CryptoEngine.fromHex(mission.coordinatorPublicKeyHex)
            val canonicalData = envelope.canonicalHeaderBytes()

            CryptoEngine.verify(pubKeyBytes, canonicalData, signatureBytes)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if a user role has permission to assign responders to incidents.
     */
    fun canAssignResponders(role: Role): Boolean {
        return role == Role.COORDINATOR || role == Role.STAFF
    }

    /**
     * Checks if a user role can trigger general broadcasts.
     */
    fun canBroadcast(role: Role): Boolean {
        return role == Role.COORDINATOR
    }
}
