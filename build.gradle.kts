import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    java
    idea
    id("net.minecraftforge.gradle") version "5.1.+"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"
}

// Allows local configuration for a dev environment that importantly, isn't present on a build server.
// Edit the dev.gradle.kts file and define any of the below properties which use project.findProperty() under extra.apply { }, e.g.
//
// extra.apply {
//     set("minify_resources", false)
// }
// Properties that can be configured this way are:
// - "minify_resources" (to save time in dev)
// - "mappings_channel", "mappings_version" (for better mappings in dev)
// - "use_advanced_class_redefinition" (if using the Jetbrains Runtime JDK and want to enable -XX:+AllowEnhancedClassRedefinition for super amazing hotswap)
File("./dev.gradle.kts").createNewFile()
apply(from = "dev.gradle.kts")

// Toolchain versions
val minecraftVersion: String = "1.18.2"
val forgeVersion: String = "40.2.14"
val mixinVersion: String = "0.8.5"

val modId: String = "customreject"
val modVersion: String = "1.0.0"

// Optional dev-env properties
val mappingsChannel: String = project.findProperty("mappings_channel") as String? ?: "official"
val mappingsVersion: String = project.findProperty("mappings_version") as String? ?: minecraftVersion
val minifyResources: Boolean = project.findProperty("minify_resources") as Boolean? ?: false
val useAdvancedClassRedef: Boolean = project.findProperty("use_advanced_class_redefinition") as Boolean? ?: false

println("Using mappings $mappingsChannel / $mappingsVersion with version $modVersion")

base {
    archivesName.set("CustomReject-Forge-$minecraftVersion")
    group = "su.external.customreject"
    version = modVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    flatDir {
        dirs("libs")
    }
}

dependencies {
    minecraft("net.minecraftforge", "forge", version = "$minecraftVersion-$forgeVersion")

    if (System.getProperty("idea.sync.active") != "true") {
        annotationProcessor("org.spongepowered:mixin:${mixinVersion}:processor")
    }
}

minecraft {
    mappings(mappingsChannel, mappingsVersion)
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        all {
            args("-mixin.config=$modId.mixins.json")

            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modId)

            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")

            jvmArgs("-ea", "-Xmx4G", "-Xms4G")

            if (useAdvancedClassRedef) {
                jvmArg("-XX:+AllowEnhancedClassRedefinition")
            }


            mods.create(modId) {
                source(sourceSets.main.get())
                source(sourceSets.test.get())
            }
        }

        register("client") {
            workingDirectory("run/client")
        }

        register("server") {
            workingDirectory("run/server")

            arg("--nogui")
        }

    }
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
}


tasks {

    processResources {

        filesMatching("**/book.json") {
            expand(mapOf("version" to project.version))
        }

        if (minifyResources) {
            doLast {
                val jsonMinifyStart: Long = System.currentTimeMillis()
                var jsonMinified: Long = 0
                var jsonBytesBefore: Long = 0
                var jsonBytesAfter: Long = 0

                fileTree(mapOf("dir" to outputs.files.asPath, "include" to "**/*.json")).forEach {
                    jsonMinified++
                    jsonBytesBefore += it.length()
                    try {
                        it.writeText(JsonOutput.toJson(JsonSlurper().parse(it)).replace("\"__comment__\":\"This file was automatically created by mcresources\",", ""))
                    } catch (e: Exception) {
                        println("JSON Error in ${it.path}")
                        throw e
                    }

                    jsonBytesAfter += it.length()
                }
                println("Minified $jsonMinified json files. Reduced ${jsonBytesBefore / 1024} kB to ${(jsonBytesAfter / 1024)} kB. Took ${System.currentTimeMillis() - jsonMinifyStart} ms")
            }
        }
    }

    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
            attributes["MixinConfigs"] = "$modId.mixins.json"
        }
    }

    register("generateSources", Copy::class) {
        from("src/main/java/")
        into("${buildDir}/generated-src")
        filesMatching("**/CustomReject.java") {
            expand(mapOf("version" to project.version))
        }
    }

    compileJava {
        setSource("${buildDir}/generated-src")
        dependsOn("generateSources")
    }
}

