/*
 * This file is part of HKBusETA.
 *
 * Copyright (C) 2026. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2026. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
}

group = "com.loohp.hkbuseta"
version = "1.0.0"

tasks.jar.configure {
    manifest {
        attributes(mapOf("Main-Class" to "com.loohp.hkbuseta.notificationserver.NotificationServerKt"))
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    excludes += "META-INF/*.SF"
    excludes += "META-INF/*.DSA"
    excludes += "META-INF/*.RSA"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

sourceSets {
    main {
        output.setResourcesDir("build/classes/main")
    }
    test {
        output.setResourcesDir("build/classes/test")
    }
}

dependencies {
    implementation(libs.guava)
    implementation(libs.firebase.admin)
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.java)
    implementation(libs.ktor.client.encoding)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.stately.concurrency)
    implementation(libs.stately.concurrent.collections)
    implementation(libs.xmlCore)
    implementation(libs.serialization.xml)
    implementation(projects.shared)
}