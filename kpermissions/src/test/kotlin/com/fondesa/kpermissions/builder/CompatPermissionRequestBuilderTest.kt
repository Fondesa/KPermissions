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

package com.fondesa.kpermissions.builder

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.manifest.ManifestPermissionRequest
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.RuntimePermissionRequest
import com.fondesa.test.TestActivity
import com.fondesa.test.launchTestActivity
import com.fondesa.test.letActivity
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.annotation.Config

/**
 * Tests for [CompatPermissionRequestBuilderTest].
 */
@RunWith(AndroidJUnit4::class)
class CompatPermissionRequestBuilderTest {
    private val provider = mock<RuntimePermissionHandlerProvider> {
        on(it.provideHandler()).thenReturn(mock())
    }
    private lateinit var builder: CompatPermissionRequestBuilder
    private lateinit var scenario: ActivityScenario<TestActivity>

    @Before
    fun createBuilder() {
        scenario = launchTestActivity()
        builder = scenario.letActivity { CompatPermissionRequestBuilder(it) }
    }

    @After
    fun destroyScenario() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
    }

    @Config(maxSdk = 22)
    @Test
    fun verifyRequestBelowApi23() {
        // Build the request.
        val request = builder.permissions("example")
            .runtimeHandlerProvider(provider)
            .build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
        assertThat(request, instanceOf(ManifestPermissionRequest::class.java))
    }

    @Config(minSdk = 23)
    @Test
    fun verifyRequestSinceApi23() {
        // Build the request.
        val request = builder.permissions("example", "example_2")
            .runtimeHandlerProvider(provider)
            .build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
        assertThat(request, instanceOf(RuntimePermissionRequest::class.java))
    }
}
