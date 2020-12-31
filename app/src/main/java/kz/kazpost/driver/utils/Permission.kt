package kz.kazpost.driver.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import pub.devrel.easypermissions.EasyPermissions

class Permission{
    private val tag = "Permission message"
    private var PERMISSION_ALL = 1

    private var PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA
    )

    fun askPermission(context: Context): Boolean{
        return when {
            !EasyPermissions.hasPermissions(
                context,
                *PERMISSIONS
            ) -> {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    PERMISSIONS,
                    PERMISSION_ALL
                )
                true
            } else -> false
        }
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        return when (requestCode) {
            PERMISSION_ALL -> {
                return (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
            else -> {
                false
            }
        }
    }
}