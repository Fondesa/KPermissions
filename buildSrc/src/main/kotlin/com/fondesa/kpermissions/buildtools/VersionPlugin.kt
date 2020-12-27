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

package com.fondesa.kpermissions.buildtools

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.ByteArrayOutputStream

/**
 * Sets the version of the project.
 * The version by default is the name of the last tag on Git.
 * The version can be defined manually using the property "version.name".
 * E.g.
 * ./gradlew myTask -Pversion.name=1.0.0
 */
class VersionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        version = getPropertyOrElse("version.name") { getVersionNameFromTag() }
    }

    private fun Project.getVersionNameFromTag(): String {
        val stdOut = ByteArrayOutputStream()
        // Gets the latest tag in the Git repo.
        exec {
            commandLine = listOf("git", "describe", "--tags", "--abbrev=0")
            standardOutput = stdOut
        }
        return stdOut.toString().trim()
    }

    private inline fun Project.getPropertyOrElse(propertyName: String, default: () -> String): String =
        if (project.hasProperty(propertyName)) project.property(propertyName) as String else default()
}
