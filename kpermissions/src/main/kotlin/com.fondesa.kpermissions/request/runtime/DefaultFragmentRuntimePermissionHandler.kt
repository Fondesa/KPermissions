/*
 * Copyright (c) 2020 Giorgio Antonioli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("DEPRECATION")

package com.fondesa.kpermissions.request.runtime

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.checkRuntimePermissionsStatus
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Implementation of [FragmentRuntimePermissionHandler] that specifies the lifecycle of the
 * runtime permissions' requests.
 *
 * It can process maximum one permissions' request at the same. This is done to avoid multiple
 * requests handled by the OS together that will show overlapped permission's dialogs.
 *
 * If you are using the new status API:
 * The [PermissionRequest.Listener] will be notified with a number of [PermissionStatus] equals to
 * the number of permissions requested.
 * The possible status which can be notified with a runtime request are:
 * - [PermissionStatus.Granted] -> the permission is granted
 * - [PermissionStatus.Denied.ShouldShowRationale] -> the permission is denied by the user and it can be useful to
 * show a rationale explaining the motivation of this permission request
 * - [PermissionStatus.Denied.Permanently] -> the permission is permanently denied by the user using the
 * "never ask again" button on the permissions dialog.
 */
@Deprecated("Use the new ResultLauncherRuntimePermissionHandler.")
@RequiresApi(23)
public class DefaultFragmentRuntimePermissionHandler : FragmentRuntimePermissionHandler() {
    private var isProcessingPermissions = false
    private var pendingHandleRuntimePermissions: (() -> Unit)? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pendingHandleRuntimePermissions?.invoke()
        pendingHandleRuntimePermissions = null
    }

    override fun managePermissionsResult(permissions: Array<out String>, grantResults: IntArray) {
        // Now the Fragment is not processing the permissions anymore.
        isProcessingPermissions = false
        // Get the listener for this set of permissions.
        // If it's null, the permissions can't be notified.
        val listener = listenerOf(permissions) ?: return

        val result = permissions.mapIndexed { index, permission ->
            val isGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED
            if (isGranted) {
                return@mapIndexed PermissionStatus.Granted(permission)
            }
            if (shouldShowRequestPermissionRationale(permission)) {
                PermissionStatus.Denied.ShouldShowRationale(permission)
            } else {
                PermissionStatus.Denied.Permanently(permission)
            }
        }
        listener.onPermissionsResult(result)
    }

    override fun handleRuntimePermissions(permissions: Array<out String>) {
        if (isAdded) {
            handleRuntimePermissionsWhenAdded(permissions)
        } else {
            pendingHandleRuntimePermissions = {
                handleRuntimePermissionsWhenAdded(permissions)
            }
        }
    }

    private fun handleRuntimePermissionsWhenAdded(permissions: Array<out String>) {
        // Get the listener for this set of permissions.
        // If it's null, the permissions can't be notified.
        val listener = listenerOf(permissions) ?: return
        val activity = requireActivity()
        val currentStatus = activity.checkRuntimePermissionsStatus(permissions.toList())
        val areAllGranted = currentStatus.allGranted()
        if (!areAllGranted) {
            if (isProcessingPermissions) {
                // The Fragment can process only one request at the same time.
                return
            }
            // Request the permissions.
            requestRuntimePermissions(permissions)
        } else {
            listener.onPermissionsResult(currentStatus)
        }
    }

    @Suppress("OverridingDeprecatedMember")
    override fun requestRuntimePermissions(permissions: Array<out String>) {
        // The Fragment is now processing some permissions.
        isProcessingPermissions = true
        Log.d(TAG, "requesting permissions: ${permissions.joinToString()}")
        requestPermissions(permissions)
    }
}
