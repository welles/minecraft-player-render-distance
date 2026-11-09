plugins {
    id("net.neoforged.moddev") version "2.0.148"
    id("neoforge-mutex")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-neoforge"

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

neoForge {
    version = property("deps.neo_loader") as String

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            gameDirectory = file("../../run/")
            client()
        }

        register("server") {
            gameDirectory = file("../../run/")
            server()
        }

        // A local server and two clients that join it, for scripts/test-multiplayer.sh. Each in its own directory,
        // so they do not share options, configs or logs.
        register("testServer") {
            gameDirectory = file("../../run/test/server")
            server()
        }
        for ((name, player) in listOf("testClientA" to "Alice", "testClientB" to "Bob")) {
            register(name) {
                gameDirectory = file("../../run/test/$player")
                client()
                programArguments.addAll("--username", player, "--quickPlayMultiplayer", "localhost:25565")
            }
        }
    }
}

// The test clients wait until the test server accepts connections, so they can join it right away
tasks.matching { it.name == "runTestClientA" || it.name == "runTestClientB" }.configureEach {
    doFirst { waitForTestServer() }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, value: String) {
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", sc.properties["mod.id"])
            register("name", sc.properties["mod.name"])
            register("version", sc.properties["mod.version"])
            register("minecraft", sc.properties["mod.mc_compat"])
            // The square icon in the mod list: iconFile since NeoForge 26.2, the now deprecated logoFile before
            register("icon", if (sc.current.parsed >= "26.2") "icon" else "logo")
        }

        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        // Which players are sent to which other players
        val mixins = listOf("TrackedEntityMixin").joinToString(", ") { "\"$it\"" }
        val clientMixins = buildList {
            // The entity distance from the video settings
            add("EntityMixin")
            // Other players far away are drawn closer and smaller, out of the fog
            add("EntityRenderDispatcherMixin")
            // Since 1.21.9 entities are drawn from render states, which remember whether they belong to another player
            if (sc.current.parsed >= "1.21.9") addAll(listOf("EntityRenderStateMixin", "EntityRendererMixin"))
            // Entities are only drawn in loaded and compiled chunk sections, checked by the LevelExtractor since 26.2
            add(if (sc.current.parsed >= "26.2") "LevelExtractorMixin" else "LevelRendererMixin")
            // The far plane of the camera, calculated by the Camera since 26.1
            add(if (sc.current.parsed >= "26.1") "CameraMixin" else "GameRendererMixin")
        }.joinToString(", ") { "\"$it\"" }
        inputs.property("mixins", mixins)
        inputs.property("clientMixins", clientMixins)
        filesMatching("*.mixins.json") { expand("java" to mixinJava, "mixins" to mixins, "clientMixins" to clientMixins) }

        exclude("fabric.mod.json")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    withType<Jar> {
        val name = project.property("mod.id")
        inputs.property("mod_id", name)
        from("../../LICENSE") { rename { "$it-$name" } }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(jar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}
