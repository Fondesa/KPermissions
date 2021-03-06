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

package com.fondesa.kpermissions.request

/**
 * Base implementation of [PermissionRequest] that implements all the listeners' logic
 * that must be the same to all its subclasses.
 */
public abstract class BasePermissionRequest : PermissionRequest {
    protected val listeners: MutableSet<PermissionRequest.Listener> = mutableSetOf()

    override fun addListener(listener: PermissionRequest.Listener) {
        listeners += listener
    }

    override fun removeListener(listener: PermissionRequest.Listener) {
        listeners -= listener
    }

    override fun removeAllListeners() {
        listeners.clear()
    }
}
