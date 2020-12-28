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

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.jfrog.bintray.gradle.BintrayExtension
import java.util.Date
import java.util.Properties
import java.util.regex.Pattern
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask

/**
 * Deploys this library to jCenter and Maven Central through Bintray.
 * The public deploy properties are defined in the file "deploy.properties".
 * The private deploy properties aren't versioned.
 * The version which should be deployed is defined through [VersionPlugin].
 */
class DeployPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        apply(plugin = "maven-publish")
        apply(plugin = "com.jfrog.bintray")

        changeAarFileName()
        val deployProperties = readPropertiesOf("deploy.properties")
        val sourcesJarTask = registerSourcesJarTask()
        val javadocJarTask = registerJavadocJarTask()
        val sourcesJarArchive = artifacts.add("archives", sourcesJarTask)
        val javadocJarArchive = artifacts.add("archives", javadocJarTask)
        configureMavenPublication(deployProperties, javadocJarArchive, sourcesJarArchive)
        configureBintrayUpload(deployProperties)
        configureGitHubReleaseExtension()
        registerPublishLibraryTask()
    }

    private val Project.aarFileName: String get() = "$name-$versionName.aar"

    private fun Project.changeAarFileName() {
        configure<LibraryExtension> {
            libraryVariants.all {
                outputs.all {
                    (this as BaseVariantOutputImpl).outputFileName = aarFileName
                }
            }
        }
    }

    private fun Project.registerSourcesJarTask(): TaskProvider<out Task> {
        val sourcesJarTask = tasks.register<Jar>("sourcesJar")
        configure<BaseExtension> {
            sourceSets.named("main") {
                sourcesJarTask.configure {
                    archiveClassifier.set("sources")
                    from(java.srcDirs)
                }
            }
        }
        return sourcesJarTask
    }

    private fun Project.registerJavadocJarTask(): TaskProvider<out Task> {
        val javadocJarTask = tasks.register<Jar>("javadocJar")
        javadocJarTask.dependsOn("dokka")
        tasks.withType<DokkaTask> {
            javadocJarTask.configure {
                archiveClassifier.set("javadoc")
                from(outputDirectory)
            }
        }
        return javadocJarTask
    }

    private fun Project.configureMavenPublication(
        deployProperties: Properties,
        javadocJarArchive: PublishArtifact,
        sourcesJarArchive: PublishArtifact
    ) {
        configure<PublishingExtension> {
            publications {
                register<MavenPublication>("libraryPublication") {
                    artifact(javadocJarArchive)
                    artifact(sourcesJarArchive)
                    artifact("$buildDir/outputs/aar/$aarFileName")
                    groupId = deployProperties.getProperty("group.id")
                    artifactId = deployProperties.getProperty("artifact.id")
                    version = versionName
                    pom { configureMavenPom(this, deployProperties) }
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.configureMavenPom(pom: MavenPom, deployProperties: Properties) {
        pom.name.set(deployProperties.getProperty("lib.name"))
        pom.description.set(deployProperties.getProperty("lib.description"))
        pom.url.set(deployProperties.getProperty("site.url"))
        pom.licenses {
            license {
                name.set(deployProperties.getProperty("license.name"))
                url.set(deployProperties.getProperty("license.url"))
            }
        }
        pom.developers {
            developer {
                id.set(deployProperties.getProperty("developer.id"))
                name.set(deployProperties.getProperty("developer.name"))
                email.set(deployProperties.getProperty("developer.mail"))
            }
        }
        pom.scm {
            connection.set(deployProperties.getProperty("git.url"))
            developerConnection.set(deployProperties.getProperty("git.url"))
            url.set(deployProperties.getProperty("site.url"))
        }
        configureMavenPomDependencies(pom, deployProperties)
    }

    private fun Project.configureMavenPomDependencies(pom: MavenPom, deployProperties: Properties) {
        pom.withXml {
            val dependenciesNode = asNode().appendNode("dependencies")
            val exportedConfigurationsNames = setOf("compile", "implementation", "api")
            val addedDependencies = mutableMapOf<String, Dependency>()
            configurations.configureEach {
                if (name !in exportedConfigurationsNames) {
                    return@configureEach
                }
                val configDependencies = allDependencies
                    .filter { dependency -> dependency.group != null && dependency.version != null }
                    .filter { dependency -> addedDependencies[dependency.name] == null }
                    .map { dependency -> dependency.name to dependency }
                    .toMap()

                configDependencies.values.forEach { dependency ->
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    if (project.isDependencyLocal(dependency)) {
                        val dependencyProject = rootProject.project(dependency.name)
                        val dependencyDeployProperties = dependencyProject.readPropertiesOf("deploy.properties")
                        dependencyNode.appendNode("groupId", deployProperties.getProperty("group.id"))
                        dependencyNode.appendNode("artifactId", dependencyDeployProperties.getProperty("artifact.id"))
                        dependencyNode.appendNode("version", version)
                    } else {
                        dependencyNode.appendNode("groupId", dependency.group)
                        dependencyNode.appendNode("artifactId", dependency.name)
                        dependencyNode.appendNode("version", dependency.version)
                    }
                }
                addedDependencies += configDependencies
            }
        }
    }

    private fun Project.configureBintrayUpload(deployProperties: Properties) {
        configure<BintrayExtension> {
            user = deployProperties.getProperty("bintray.username")
            key = getStringProperty("bintray.api.key") ?: System.getenv("BINTRAY_API_KEY")
            publish = true
            setPublications("libraryPublication")
            pkg.also { bintrayPkg ->
                bintrayPkg.repo = deployProperties.getProperty("bintray.repo")
                bintrayPkg.name = deployProperties.getProperty("lib.name")
                bintrayPkg.desc = deployProperties.getProperty("lib.description")
                bintrayPkg.websiteUrl = deployProperties.getProperty("site.url")
                bintrayPkg.issueTrackerUrl = deployProperties.getProperty("issue.tracker.url")
                bintrayPkg.vcsUrl = deployProperties.getProperty("git.url")
                bintrayPkg.setLicenses(deployProperties.getProperty("license.id"))
                bintrayPkg.isPublicDownloadNumbers = true
                val tags = deployProperties.getProperty("lib.tags")
                bintrayPkg.setLabels(*tags.split(Pattern.quote("|")).toTypedArray())
                bintrayPkg.githubRepo = deployProperties.getProperty("github.repo")
                bintrayPkg.version.also { version ->
                    version.name = versionName
                    version.released = Date().toString()
                    version.desc = deployProperties.getProperty("version.description")
                    version.gpg.also { gpg ->
                        gpg.sign = true
                        gpg.passphrase = getStringProperty("bintray.gpg.password") ?: System.getenv("BINTRAY_GPG_PASSWORD")
                    }
                    version.mavenCentralSync.also { mavenCentral ->
                        mavenCentral.sync = true
                        mavenCentral.user = getStringProperty("maven.central.username") ?: System.getenv("MAVEN_CENTRAL_USERNAME")
                        mavenCentral.password = getStringProperty("maven.central.password") ?: System.getenv("MAVEN_CENTRAL_PASSWORD")
                    }
                }
            }
        }
    }

    private fun Project.configureGitHubReleaseExtension() {
        rootProject.configure<GithubReleaseExtension> {
            releaseAssets.from(
                "$buildDir/outputs/aar/$aarFileName",
                "$buildDir/libs/$name-$versionName-javadoc.jar",
                "$buildDir/libs/$name-$versionName-sources.jar"
            )
        }
    }

    private fun Project.registerPublishLibraryTask() {
        tasks.register("publishLibrary") {
            dependsOn("clean")
            dependsOn("assembleRelease")
            dependsOn("sourcesJar")
            dependsOn("javadocJar")
            dependsOn("generatePomFileForLibraryPublicationPublication")
            finalizedBy("bintrayUpload")
            finalizedBy(rootProject.tasks.named("githubRelease"))
        }

        tasks.configureEach {
            when (name) {
                "assembleRelease" -> mustRunAfter("clean")
                "sourcesJar" -> mustRunAfter("assembleRelease")
                "javadocJar" -> mustRunAfter("sourcesJar")
                "generatePomFileForLibraryPublicationPublication" -> mustRunAfter("javadocJar")
            }
        }
    }

    private fun Project.isDependencyLocal(dependency: Dependency): Boolean = dependency.group == rootProject.name

    private val Project.versionName: String get() = version as String

    private fun Project.getStringProperty(propertyName: String): String? =
        if (project.hasProperty(propertyName)) project.property(propertyName) as String else null
}
