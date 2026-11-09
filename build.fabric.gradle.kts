plugins {
    // Applies the correct Loom variant based on the Minecraft version
    id("dev.kikugie.loom-back-compat")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-fabric"

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

repositories {
    // Mod Menu
    maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    // Applies Mojang Mappings on obfuscated versions
    loomx.applyMojangMappings()

    // Use `mod{dependency type}` even on 26.1+ - loom-back-compat converts them
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    // Not needed by the mod; only in the development runs, for Mod Menu and other mods tried together with this one
    modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    // Config button in Mod Menu, only used when Mod Menu is installed
    modCompileOnly("com.terraformersmc:modmenu:${property("deps.modmenu")}") { isTransitive = false }
    modLocalRuntime("com.terraformersmc:modmenu:${property("deps.modmenu")}") { isTransitive = false }
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    runConfigs.configureEach {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
    }

    // A local server and two clients that join it, for scripts/test-multiplayer.sh. Each in its own directory, so
    // they do not share options, configs or logs.
    runConfigs.register("testServer") {
        server()
        generateRunConfig = false
        runDirectory = rootProject.file("run/test/server")
    }
    for ((name, player) in listOf("testClientA" to "Alice", "testClientB" to "Bob")) {
        runConfigs.register(name) {
            client()
            generateRunConfig = false
            runDirectory = rootProject.file("run/test/$player")
            programArgs("--username", player, "--quickPlayMultiplayer", "localhost:25565")
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
            register("loader", sc.properties["mod.min_fabric_loader"])
        }

        filesMatching("fabric.mod.json") { expand(props) }

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

        exclude("META-INF/neoforge.mods.toml")
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
        from(loomx.modJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}
