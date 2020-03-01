/*
 * Copyright (c) 2018 Fondesa
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

package com.fondesa.kpermissions.extension

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.builder.CompatPermissionRequestBuilder
import com.fondesa.kpermissions.builder.PermissionRequestBuilder
import com.fondesa.kpermissions.request.runtime.FragmentRuntimePermissionHandlerProvider

/**
 * Creates the default [PermissionRequestBuilder] using the context of the [Activity].
 * The builder will use the default configurations and will be provided with
 * the set of [otherPermissions] attached to it.
 *
 * @param otherPermissions set of permissions that must be attached to the builder.
 * @return new instance of the default [PermissionRequestBuilder].
 */
fun FragmentActivity.permissionsBuilder(firstPermission: String, vararg otherPermissions: String): PermissionRequestBuilder {
    val handler = FragmentRuntimePermissionHandlerProvider(supportFragmentManager)
    // Creates the builder.
    return CompatPermissionRequestBuilder(this)
        .permissions(firstPermission, *otherPermissions)
        .runtimeHandlerProvider(handler)
}

internal fun Activity.checkPermissionsStatus(permissions: List<String>): List<PermissionStatus> =
    permissions.map { permission ->
        if (isPermissionGranted(permission)) {
            return@map PermissionStatus.Granted(permission)
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            PermissionStatus.Denied.Permanently(permission)
        } else {
            PermissionStatus.RequestRequired(permission)
        }
    }