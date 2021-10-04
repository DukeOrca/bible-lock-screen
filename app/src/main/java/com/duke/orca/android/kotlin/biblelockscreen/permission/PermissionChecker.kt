package com.duke.orca.android.kotlin.biblelockscreen.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.MainApplication
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

internal object PermissionChecker {
    private var snackbar: Snackbar? = null

    fun checkPermission(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    internal fun checkPermissions(context: Context, permissions: List<String>): Boolean {
        permissions.forEach {
            with(checkPermission(context, it)) {
                if (this.not())
                    return false
            }
        }

        return true
    }

    internal fun checkPermissions(
        context: Context,
        permissions: List<String>,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit,
        onPermissionChecked: (() -> Unit)? = null
    ) {
        Dexter.withContext(context)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.grantedPermissionResponses.map { it.permissionName }.containsAll(permissions)) {
                        onPermissionGranted.invoke()
                    }

                    if (report.deniedPermissionResponses.map { it.permissionName }.containsAll(permissions)) {
                        onPermissionDenied.invoke()
                    }

                    onPermissionChecked?.invoke()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    internal fun hasManageOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(MainApplication.getApplicationContext())
    }

    fun showSnackbar(
        view: View,
        text: String,
        actionText: String,
        activityResultLauncher: ActivityResultLauncher<Intent>?
    ) {
        val context = view.context

        snackbar?.dismiss()

        snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
            .setAction(actionText) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", it.context.packageName, null)

                activityResultLauncher?.launch(intent.apply { data = uri })
            }
            .setActionTextColor(ContextCompat.getColor(context, R.color.text))

        snackbar?.show()
    }

    fun dismissSnackbar(): Boolean {
        if (snackbar?.isShownOrQueued == true) {
            snackbar?.dismiss()
            return true
        }

        return false
    }
}