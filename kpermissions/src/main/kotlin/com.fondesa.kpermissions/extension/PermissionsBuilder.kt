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

package com.fondesa.kpermissions.extension

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.fondesa.kpermissions.builder.CompatPermissionRequestBuilder
import com.fondesa.kpermissions.builder.PermissionRequestBuilder
import com.fondesa.kpermissions.request.runtime.FragmentRuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.ResultLauncherRuntimePermissionHandlerProvider
import com.fondesa.kpermissions.shouldUseLegacyRuntimePermissionHandler

/**
 * Creates the default [PermissionRequestBuilder] using the context of the [Activity].
 * The builder will use the default configurations and will be provided with
 * the set of [otherPermissions] attached to it.
 *
 * @param firstPermission the first permission which should be requested.
 * @param otherPermissions the other permissions that must be requested, if the request
 * should handle more than one permission.
 * @return new instance of the default [PermissionRequestBuilder].
 */
public fun FragmentActivity.permissionsBuilder(
    firstPermission: String,
    vararg otherPermissions: String
): PermissionRequestBuilder {
    val handler = if (shouldUseLegacyRuntimePermissionHandler) {
        FragmentRuntimePermissionHandlerProvider(supportFragmentManager)
    } else {
        ResultLauncherRuntimePermissionHandlerProvider(supportFragmentManager)
    }
    // Creates the builder.
    return CompatPermissionRequestBuilder(this)
        .permissions(firstPermission, *otherPermissions)
        .runtimeHandlerProvider(handler)
}

/**
 * Creates the default [PermissionRequestBuilder] using the context of the [Activity] at which
 * this [Fragment] is attached.
 * The builder will use the default configurations and will be provided with
 * the set of [firstPermission] plus [otherPermissions] attached to it.
 *
 * @param firstPermission the first permission which should be requested.
 * @param otherPermissions the other permissions that must be requested, if the request
 * should handle more than one permission.
 * @return new instance of the default [PermissionRequestBuilder].
 * @throws NullPointerException if the [Fragment] is not attached to an [Activity].
 */
public fun Fragment.permissionsBuilder(
    firstPermission: String,
    vararg otherPermissions: String
): PermissionRequestBuilder = requireActivity().permissionsBuilder(firstPermission, *otherPermissions)
