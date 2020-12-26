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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    apply(from = "repositories.gradle", to = buildscript)
    apply(from = "parse-dependencies.gradle.kts")

    val deps: Map<String, String> by project.extra
    dependencies {
        classpath(deps.getValue("kotlinPlugin"))
        classpath(deps.getValue("ktlint"))
    }
}

apply(from = "repositories.gradle")
apply(plugin = "kotlin")
apply(from = "ktlint.gradle")

// We can't apply kotlin.gradle because otherwise the warnings will be treated as errors.
// Since Kotlin 1.4.x, we can't compile buildSrc because the Gradle Wrapper contains an old Kotlin version internally.
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        allWarningsAsErrors = false
        freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
    }
}

val deps: Map<String, String> by project.extra
dependencies {
    implementation(deps.getValue("androidPlugin"))
    implementation(deps.getValue("bintrayPlugin"))
    implementation(deps.getValue("dokkaPlugin"))
    implementation(deps.getValue("gitHubReleasePlugin"))
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.squareup.okhttp3" && requested.name == "okhttp") {
            useVersion("4.0.0")
            because("Fixes \"com.github.breadmoirai:github-release\" since it doesn't depend on OkHttp 4.")
        }
    }
}
