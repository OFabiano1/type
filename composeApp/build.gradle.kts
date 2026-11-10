import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.1.21"
    id("org.jetbrains.compose") version "1.8.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
}

group = "app.type"
version = "0.3.1"

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

// Tree do spec usa src/jvmMain|jvmTest (estilo KMP); plugin jvm usa src/main|test por padrão — wiring mantém a árvore desenhada.
sourceSets {
    main {
        kotlin.srcDir("src/jvmMain/kotlin")
        resources.srcDir("src/jvmMain/resources")
    }
    test {
        kotlin.srcDir("src/jvmTest/kotlin")
    }
}


dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    testImplementation(kotlin("test"))
    testImplementation(compose.desktop.uiTestJUnit4)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "app.type.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "TYPE"
            packageVersion = "0.3.1"

            windows {
                iconFile.set(project.rootProject.projectDir.resolve("logo.ico"))
            }
        }
    }
}
