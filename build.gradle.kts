import groovy.sql.Sql
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.postgresql.ds.PGSimpleDataSource

buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.2.4")
    }
}

plugins {
    kotlin("jvm") version "1.2.51"
    application
}

group = "org"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "io.ktor.server.netty.DevelopmentEngine"
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://kotlin.bintray.com/ktor")
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${ext["kotlin_version"]}")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ext["kotlinx_coroutines_version"]}")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-io:${ext["kotlinx_coroutines_version"]}")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${ext["kotlinx_coroutines_version"]}")

    compile("io.ktor:ktor-server-netty:${ext["ktor_version"]}")
    compile("ch.qos.logback:logback-classic:${ext["logback_version"]}")
    compile("io.ktor:ktor-server-core:${ext["ktor_version"]}")
    compile("io.ktor:ktor-html-builder:${ext["ktor_version"]}")
    compile("io.ktor:ktor-server-host-common:${ext["ktor_version"]}")
    compile("io.ktor:ktor-metrics:${ext["ktor_version"]}")
    compile("io.ktor:ktor-auth:${ext["ktor_version"]}")
    compile("io.ktor:ktor-jackson:${ext["ktor_version"]}")

    testCompile("io.ktor:ktor-server-tests:${ext["ktor_version"]}")
    testCompile("io.ktor:ktor-server-core:${ext["ktor_version"]}")
    testCompile("io.ktor:ktor-html-builder:${ext["ktor_version"]}")
    testCompile("io.ktor:ktor-server-host-common:${ext["ktor_version"]}")
    testCompile("io.ktor:ktor-metrics:${ext["ktor_version"]}")
    testCompile("io.ktor:ktor-auth:${ext["ktor_version"]}")
    testCompile("io.ktor:ktor-jackson:${ext["ktor_version"]}")
}

ant.importBuild("build.xml") { "ant-$it" }

val sql = dataSource {
    setURL("jdbc:postgresql://localhost:5432/ale")
    user = "postgres"
    password = "postgres"
}

tasks {
    "db-ddl" {
        group = "database"
        doFirst { sqlFileExe("database/ddl.sql") }
    }

    "db-build" {
        group = "database"
        dependsOn("db-ddl")
        doFirst { sqlFileExe("database/data.sql") }
        doLast { sql.close() }
    }
}

fun dataSource(op: PGSimpleDataSource.() -> Unit) = Sql(PGSimpleDataSource().also(op))

fun sqlFileExe(file: String) = sql.execute(File(file).readText())