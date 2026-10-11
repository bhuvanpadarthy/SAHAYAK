package org.sahayak.mesh.core.model

enum class Priority(val weight: Int) {
    EMERGENCY(100),
    HIGH(75),
    NORMAL(50),
    LOW(25)
}

enum class PayloadType {
    CHAT_DIRECT,
    BROADCAST_ALERT,
    DELIVERY_ACK,
    SOS_ALERT,
    HEARTBEAT,
    MISSION_CONTROL
}

enum class Role {
    COORDINATOR,
    RESPONDER,
    VOLUNTEER,
    STAFF,
    RELAY,
    VISITOR
}

enum class AlertLevel {
    ADVISORY,
    WARNING,
    CRITICAL
}

enum class IncidentCategory {
    MEDICAL,
    EVACUATION,
    ACCESSIBILITY,
    GENERAL
}

enum class IncidentStatus {
    OPEN,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CANCELLED
}

enum class TrustLevel {
    UNKNOWN,
    UNVERIFIED,
    VERIFIED_IN_PERSON,
    BLOCKED,
    REVOKED
}
