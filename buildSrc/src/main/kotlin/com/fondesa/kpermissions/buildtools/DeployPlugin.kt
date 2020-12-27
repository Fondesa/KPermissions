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
 * Deploys this library to jCenter and Maven Central through Bintray.
 * The public deploy properties are defined in the file "deploy.properties".
 * The private deploy properties aren't versioned.
 * The version which should be deployed is defined through [VersionPlugin].
 */
class DeployPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
//        plugins.apply("maven-publish")
//        plugins.apply("com.jfrog.bintray")
//
//        changeAarFileName()
//        val deployProperties = readPropertiesOf("deploy.properties")
//        val sourcesJarTask = registerSourcesJarTask()
//        val javadocJarTask = registerJavadocJarTask()
//        val sourcesJarArchive = artifacts.add("archives", sourcesJarTask)
//        val javadocJarArchive = artifacts.add("archives", javadocJarTask)
//        configureMavenPublication(deployProperties, javadocJarArchive, sourcesJarArchive)
//        configureBintrayUpload(deployProperties)
//        configureGitHubReleaseExtension()
//        registerPublishLibraryTask()
//        Unit
    }
//
//    private val Project.aarFileName: String get() = "$name-$versionName.aar"
//
//    private fun Project.changeAarFileName() {
//        withAndroidPlugin {
//            (this as? LibraryExtension)?.run {
//                libraryVariants.all { variant ->
//                    variant.outputs.all { output ->
//                        (output as BaseVariantOutputImpl).outputFileName = aarFileName
//                    }
//                }
//            }
//        }
//    }
//
//    private fun Project.registerSourcesJarTask(): TaskProvider<out Task> {
//        val sourcesJarTask = tasks.register("sourcesJar", Jar::class.java)
//        withAndroidPlugin {
//            sourceSets.named("main") { sourceSet ->
//                sourcesJarTask.configure { task ->
//                    task.archiveClassifier.set("sources")
//                    task.from(sourceSet.java.srcDirs)
//                }
//            }
//        }
//        return sourcesJarTask
//    }
//
//    private fun Project.registerJavadocJarTask(): TaskProvider<out Task> {
//        val javadocJarTask = tasks.register("javadocJar", Jar::class.java)
//        javadocJarTask.dependsOn("dokka")
//        tasks.withType(DokkaTask::class.java) { dokkaTask ->
//            javadocJarTask.configure { task ->
//                task.archiveClassifier.set("javadoc")
//                task.from(dokkaTask.outputDirectory)
//            }
//        }
//        return javadocJarTask
//    }
//
//    private fun Project.configureMavenPublication(
//        deployProperties: Properties,
//        javadocJarArchive: PublishArtifact,
//        sourcesJarArchive: PublishArtifact
//    ) {
//        extensions.configure(PublishingExtension::class.java) { publishingExtension ->
//            publishingExtension.publications { publicationContainer ->
//                publicationContainer.register(
//                    "libraryPublication",
//                    MavenPublication::class.java
//                ) { publication ->
//                    publication.artifact(javadocJarArchive)
//                    publication.artifact(sourcesJarArchive)
//                    publication.artifact("$buildDir/outputs/aar/$aarFileName")
//                    publication.groupId = deployProperties.getProperty("group.id")
//                    publication.artifactId = deployProperties.getProperty("artifact.id")
//                    publication.version = versionName
//                    publication.pom { pom -> configureMavenPom(pom, deployProperties) }
//                }
//            }
//        }
//    }
//
//    @Suppress("UnstableApiUsage")
//    private fun Project.configureMavenPom(pom: MavenPom, deployProperties: Properties) {
//        pom.name.set(deployProperties.getProperty("lib.name"))
//        pom.description.set(deployProperties.getProperty("lib.description"))
//        pom.url.set(deployProperties.getProperty("site.url"))
//        pom.licenses { licenseSpec ->
//            licenseSpec.license { license ->
//                license.name.set(deployProperties.getProperty("license.name"))
//                license.url.set(deployProperties.getProperty("license.url"))
//            }
//        }
//        pom.developers { developerSpec ->
//            developerSpec.developer { developer ->
//                developer.id.set(deployProperties.getProperty("developer.id"))
//                developer.name.set(deployProperties.getProperty("developer.name"))
//                developer.email.set(deployProperties.getProperty("developer.mail"))
//            }
//        }
//        pom.scm { scmSpec ->
//            scmSpec.connection.set(deployProperties.getProperty("git.url"))
//            scmSpec.developerConnection.set(deployProperties.getProperty("git.url"))
//            scmSpec.url.set(deployProperties.getProperty("site.url"))
//        }
//        configureMavenPomDependencies(pom, deployProperties)
//    }
//
//    private fun Project.configureMavenPomDependencies(pom: MavenPom, deployProperties: Properties) {
//        pom.withXml { xmlProvider ->
//            val dependenciesNode = xmlProvider.asNode().appendNode("dependencies")
//            val exportedConfigurationsNames = setOf("compile", "implementation", "api")
//            val addedDependencies = mutableMapOf<String, Dependency>()
//            configurations.configureEach { configuration ->
//                if (configuration.name !in exportedConfigurationsNames) {
//                    return@configureEach
//                }
//                val configDependencies = configuration.allDependencies
//                    .filter { dependency -> dependency.group != null && dependency.version != null }
//                    .filter { dependency -> addedDependencies[dependency.name] == null }
//                    .map { dependency -> dependency.name to dependency }
//                    .toMap()
//
//                configDependencies.values.forEach { dependency ->
//                    val dependencyNode = dependenciesNode.appendNode("dependency")
//                    if (project.isDependencyLocal(dependency)) {
//                        val dependencyProject = rootProject.project(dependency.name)
//                        val dependencyDeployProperties = dependencyProject.readPropertiesOf("deploy.properties")
//                        dependencyNode.appendNode("groupId", deployProperties.getProperty("group.id"))
//                        dependencyNode.appendNode("artifactId", dependencyDeployProperties.getProperty("artifact.id"))
//                        dependencyNode.appendNode("version", version)
//                    } else {
//                        dependencyNode.appendNode("groupId", dependency.group)
//                        dependencyNode.appendNode("artifactId", dependency.name)
//                        dependencyNode.appendNode("version", dependency.version)
//                    }
//                }
//                addedDependencies += configDependencies
//            }
//        }
//    }
//
//    private fun Project.configureBintrayUpload(deployProperties: Properties) {
//        extensions.configure(BintrayExtension::class.java) { bintray ->
//            bintray.user = deployProperties.getProperty("bintray.username")
//            bintray.key = getProperty("bintray.api.key") ?: System.getenv("BINTRAY_API_KEY")
//            bintray.publish = true
//            bintray.setPublications("libraryPublication")
//            bintray.pkg.also { bintrayPkg ->
//                bintrayPkg.repo = deployProperties.getProperty("bintray.repo")
//                bintrayPkg.name = deployProperties.getProperty("lib.name")
//                bintrayPkg.desc = deployProperties.getProperty("lib.description")
//                bintrayPkg.websiteUrl = deployProperties.getProperty("site.url")
//                bintrayPkg.issueTrackerUrl = deployProperties.getProperty("issue.tracker.url")
//                bintrayPkg.vcsUrl = deployProperties.getProperty("git.url")
//                bintrayPkg.setLicenses(deployProperties.getProperty("license.id"))
//                bintrayPkg.isPublicDownloadNumbers = true
//                val tags = deployProperties.getProperty("lib.tags")
//                bintrayPkg.setLabels(*tags.split(Pattern.quote("|")).toTypedArray())
//                bintrayPkg.githubRepo = deployProperties.getProperty("github.repo")
//                bintrayPkg.version.also { version ->
//                    version.name = versionName
//                    version.released = Date().toString()
//                    version.desc = deployProperties.getProperty("version.description")
//                    version.gpg.also { gpg ->
//                        gpg.sign = true
//                        gpg.passphrase = getProperty("bintray.gpg.password") ?: System.getenv("BINTRAY_GPG_PASSWORD")
//                    }
//                    version.mavenCentralSync.also { mavenCentral ->
//                        mavenCentral.sync = true
//                        mavenCentral.user = getProperty("maven.central.username") ?: System.getenv("MAVEN_CENTRAL_USERNAME")
//                        mavenCentral.password = getProperty("maven.central.password") ?: System.getenv("MAVEN_CENTRAL_PASSWORD")
//                    }
//                }
//            }
//        }
//    }
//
//    private fun Project.configureGitHubReleaseExtension() {
//        rootProject.extensions.configure(GithubReleaseExtension::class.java) { gitHubRelease ->
//            gitHubRelease.releaseAssets.from(
//                "$buildDir/outputs/aar/$aarFileName",
//                "$buildDir/libs/$name-$versionName-javadoc.jar",
//                "$buildDir/libs/$name-$versionName-sources.jar"
//            )
//        }
//    }
//
//    private fun Project.registerPublishLibraryTask() {
//        tasks.register("publishLibrary").configure { task ->
//            task.dependsOn("clean")
//            task.dependsOn("assembleRelease")
//            task.dependsOn("sourcesJar")
//            task.dependsOn("javadocJar")
//            task.dependsOn("generatePomFileForLibraryPublicationPublication")
//            task.finalizedBy("bintrayUpload")
//            task.finalizedBy(rootProject.tasks.named("githubRelease"))
//        }
//
//        tasks.configureEach { task ->
//            when (task.name) {
//                "assembleRelease" -> task.mustRunAfter("clean")
//                "sourcesJar" -> task.mustRunAfter("assembleRelease")
//                "javadocJar" -> task.mustRunAfter("sourcesJar")
//                "generatePomFileForLibraryPublicationPublication" -> task.mustRunAfter("javadocJar")
//            }
//        }
//    }
//
//    private fun Project.isDependencyLocal(dependency: Dependency): Boolean = dependency.group == rootProject.name
//
//    private val Project.versionName: String get() = version as String
//
//    private fun Project.getProperty(propertyName: String): String? =
//        if (project.hasProperty(propertyName)) project.property(propertyName) as String else null
}
