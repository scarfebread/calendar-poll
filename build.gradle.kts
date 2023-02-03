import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val kotlinVersion = "1.8.10"
val serializationVersion = "1.3.0"
val ktorVersion = "2.0.1"
val logbackVersion = "1.2.3"
val reactVersion = "17.0.2-pre.265-kotlin-1.5.31"
val awsVersion = "2.17.175"

plugins {
    kotlin("multiplatform") version "1.8.10"
    application
    kotlin("plugin.serialization") version "1.8.10"
}

group = "uk.co.scarfebread"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("http://packages.confluent.io/maven/")
        isAllowInsecureProtocol = true
    }
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser {
            binaries.executable()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-sessions:$ktorVersion")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-server-compression:$ktorVersion")
                implementation("io.ktor:ktor-server-cors:$ktorVersion")
                implementation("io.ktor:ktor-server-websockets:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("software.amazon.awssdk:dynamodb:$awsVersion")

                implementation("org.apache.kafka:kafka-clients:2.3.0")
                implementation("io.confluent:kafka-json-serializer:7.3.1")

                implementation("org.everit.json:org.everit.json.schema:1.5.1") // TODO what's this?

                // TODO might not need all of these
                implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.1")
                implementation("io.projectreactor.kafka:reactor-kafka:1.3.15")
                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:5.3.3-pre.264-kotlin-1.5.31")
                implementation(npm("styled-components", "~5.3.3"))
            }
        }
    }
}

application {
    mainClass.set("ServerKt")
}

// include JS artifacts in any JAR we generate
tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")
        || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}

tasks.create("stage") {
    dependsOn(tasks.getByName("installDist"))
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar"))
}
