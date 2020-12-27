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

/**
 * Enables the unit tests coverage in an Android project.
 */
@ExperimentalStdlibApi
class AndroidCoveragePlugin : Plugin<Project> {

    override fun apply(project: Project) = project.run {
//        apply(plugin = "jacoco")
//
//        configure<JacocoPluginExtension> {
//
//        }

//        configure<JacocoPluginExtension> {
//            toolVersion = JACOCO_VERSION
//            reportsDir = file("$buildDir/coverageReport")
//        }
//        extensions.configure(JacocoPluginExtension::class.java) {
//            it.toolVersion = JACOCO_VERSION
//            it.reportsDir = file("$buildDir/coverageReport")
//        }
//        withAndroidPlugin {
//            fixRobolectricCoverage(this)
//            configureCoverageTasks(this)
//        }
    }

//    private fun Project.configureCoverageTasks(extension: BaseExtension) {
//        afterEvaluate {
//            extension.buildTypes.all { configureCoverageTask(it, extension.sourceSets) }
//        }
//    }
//
//    private fun Project.configureCoverageTask(
//        buildType: BuildType,
//        sourceSets: NamedDomainObjectContainer<AndroidSourceSet>
//    ) {
//        val buildTypeName = buildType.name
//        val testTaskName = "test${buildTypeName.capitalize(Locale.getDefault())}UnitTest"
//        // Avoids the creation of the coverage task if the test task does not exist.
//        val testTaskProvider = tasks.namedOrNull(testTaskName) ?: return
//        val coverageTaskName = "${testTaskName}Coverage"
//        tasks.register(coverageTaskName, JacocoReport::class.java).configure { coverageTask ->
//            coverageTask.group = COVERAGE_TASKS_GROUP
//            coverageTask.description = "Calculates the coverage and generates the reports for the \"$buildTypeName\" build."
//            coverageTask.reports.apply {
//                html.isEnabled = true
//                xml.isEnabled = true
//                csv.isEnabled = false
//            }
//            val javaClassDirectories = fileTreeOf("$buildDir/intermediates/javac/$buildTypeName/classes")
//            val kotlinClassDirectories = fileTreeOf("$buildDir/tmp/kotlin-classes/$buildTypeName")
//            coverageTask.classDirectories.from(javaClassDirectories, kotlinClassDirectories)
//            coverageTask.executionData.from("$buildDir/jacoco/$testTaskName.exec")
//            sourceSets.configureEach { sourceSet ->
//                if (sourceSet.name in arrayOf("main", buildTypeName)) {
//                    coverageTask.sourceDirectories.from(sourceSet.java.srcDirs)
//                }
//            }
//        }
//        testTaskProvider.configure { task ->
//            task.finalizedBy(coverageTaskName)
//        }
//    }
//
//    private fun Project.fixRobolectricCoverage(extension: BaseExtension) {
//        extension.testOptions.unitTests.all(
//            closureOf {
//                extensions.configure(JacocoTaskExtension::class.java) {
//                    // It must be set to true otherwise the coverage of Robolectric tests is not calculated.
//                    it.isIncludeNoLocationClasses = true
//                }
//            }
//        )
//    }
//
//    private fun Project.fileTreeOf(dir: String): FileTree = fileTree(mapOf("dir" to dir, "excludes" to COVERAGE_EXCLUSIONS))

    companion object {
//        private const val JACOCO_VERSION = "0.8.5"
//        private const val COVERAGE_TASKS_GROUP = "Coverage"
    }
}
