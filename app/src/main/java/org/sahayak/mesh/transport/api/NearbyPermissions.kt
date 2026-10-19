
package org.sahayak.mesh.transport.api

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object NearbyPermissions {

    fun missingPermissions(context: Context): List<String> {
        val required = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.NEARBY_WIFI_DEVICES
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            else -> listOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        return required.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasPermissions(context: Context): Boolean =
        missingPermissions(context).isEmpty()
}
