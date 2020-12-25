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

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.releaseshub.gradle.plugin.ReleasesHubGradlePluginExtension

buildscript {
    apply(from = rootProject.file("buildSrc/repositories.gradle"), to = buildscript)
    dependencies {
        classpath(Deps.androidPlugin)
        classpath(Deps.bintrayPlugin)
        classpath(Deps.dokkaPlugin)
        classpath(Deps.gitHubReleasePlugin)
        classpath(Deps.kotlinPlugin)
        classpath(Deps.ktlint)
        classpath(Deps.releasesHubPlugin)
    }
}

allprojects {
    apply(from = rootProject.file("buildSrc/repositories.gradle"))
    apply(from = rootProject.file("buildSrc/ktlint.gradle"))
    // Adds the version of this library to all the projects, including the root project.
    apply(plugin = "kpermissions-version")
}

apply(plugin = "com.releaseshub.gradle.plugin")
configure<ReleasesHubGradlePluginExtension> {
    dependenciesBasePath = "buildSrc/src/main/kotlin/"
    dependenciesClassNames = listOf("Deps.kt")
    pullRequestEnabled = true
    pullRequestLabels = listOf("dependencies")
    headBranchPrefix = "update-dependency/"
    gitHubRepositoryOwner = "fondesa"
    gitHubRepositoryName = "kpermissions"
}

apply(plugin = "com.github.breadmoirai.github-release")
// The release assets will be configured through kpermissions-deploy.
configure<GithubReleaseExtension> {
    owner("fondesa")
    repo("kpermissions")
    tagName { project.version.toString() }
    releaseName("KPermissions $version")
    body("TBD")
    val githubToken = project.properties["github.token"]?.toString() ?: System.getenv("GITHUB_TOKEN")
    authorization.set(githubToken)
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

// Use "all" as the default distribution-type of the Gradle Wrapper.
tasks.named("wrapper", Wrapper::class) {
    distributionType = Wrapper.DistributionType.ALL
}
