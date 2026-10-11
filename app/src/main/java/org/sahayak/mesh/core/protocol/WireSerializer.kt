package org.sahayak.mesh.core.protocol

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.nio.charset.StandardCharsets

object WireSerializer {
    val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .create()

    fun serializeEnvelope(envelope: PacketEnvelope): ByteArray {
        val json = gson.toJson(envelope)
        return json.toByteArray(StandardCharsets.UTF_8)
    }

    fun deserializeEnvelope(bytes: ByteArray): PacketEnvelope? {
        return try {
            val json = String(bytes, StandardCharsets.UTF_8)
            gson.fromJson(json, PacketEnvelope::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> serializePayload(payload: T): String {
        return gson.toJson(payload)
    }

    inline fun <reified T> deserializePayload(json: String): T? {
        return try {
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
