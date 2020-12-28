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

package com.fondesa.kpermissions.buildtools

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidSourceSet
import com.android.builder.model.BuildType
import java.util.Locale
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Enables the unit tests coverage in an Android project.
 */
@ExperimentalStdlibApi
class AndroidCoveragePlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        apply(plugin = "jacoco")

        configure<JacocoPluginExtension> {
            toolVersion = JACOCO_VERSION
            reportsDir = file("$buildDir/coverageReport")
        }
        configure<BaseExtension> {
            fixRobolectricCoverage(this)
            configureCoverageTasks(this)
        }
    }

    private fun Project.configureCoverageTasks(extension: BaseExtension) {
        afterEvaluate {
            extension.buildTypes.all { configureCoverageTask(this, extension.sourceSets) }
        }
    }

    private fun Project.configureCoverageTask(
        buildType: BuildType,
        sourceSets: NamedDomainObjectContainer<AndroidSourceSet>
    ) {
        val buildTypeName = buildType.name
        val testTaskName = "test${buildTypeName.capitalize(Locale.getDefault())}UnitTest"
        // Avoids the creation of the coverage task if the test task does not exist.
        val testTaskProvider = tasks.namedOrNull(testTaskName) ?: return
        val coverageTaskName = "${testTaskName}Coverage"
        tasks.register<JacocoReport>(coverageTaskName) {
            group = COVERAGE_TASKS_GROUP
            description = "Calculates the coverage and generates the reports for the \"$buildTypeName\" build."
            reports.apply {
                html.isEnabled = true
                xml.isEnabled = true
                csv.isEnabled = false
            }
            val javaClassDirectories = fileTreeOf("$buildDir/intermediates/javac/$buildTypeName/classes")
            val kotlinClassDirectories = fileTreeOf("$buildDir/tmp/kotlin-classes/$buildTypeName")
            classDirectories.from(javaClassDirectories, kotlinClassDirectories)
            executionData.from("$buildDir/jacoco/$testTaskName.exec")
            sourceSets.configureEach {
                if (name in arrayOf("main", buildTypeName)) {
                    sourceDirectories.from(java.srcDirs)
                }
            }
        }
        testTaskProvider.configure {
            finalizedBy(coverageTaskName)
        }
    }

    private fun fixRobolectricCoverage(extension: BaseExtension) {
        extension.testOptions.unitTests.all { testTask ->
            testTask.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
            }
        }
    }

    private fun Project.fileTreeOf(dir: String): FileTree = fileTree(mapOf("dir" to dir, "excludes" to COVERAGE_EXCLUSIONS))

    companion object {
        private const val JACOCO_VERSION = "0.8.5"
        private const val COVERAGE_TASKS_GROUP = "Coverage"
    }
}
