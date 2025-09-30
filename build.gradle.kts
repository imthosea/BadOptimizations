@file:Suppress("PropertyName", "LocalVariableName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
	id("java")
	id("dev.architectury.loom") version "1.11-SNAPSHOT" apply false
	id("architectury-plugin") version "3.4-SNAPSHOT"

	id("com.gradleup.shadow") version "9.0.0" apply false
	id("io.github.pacifistmc.forgix") version "1.2.6"

	id("me.modmuss50.mod-publish-plugin") version "0.5.0"
}

val minecraft_version by properties
val java_version by properties
val mod_version by properties
val yarn_mappings by properties

architectury {
	minecraft = "$minecraft_version"
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "dev.architectury.loom")
	apply(plugin = "architectury-plugin")
	apply(plugin = "com.gradleup.shadow")

	base {
		archivesName = "BadOptimizations-${project.name}"
	}

	repositories {
		maven(url = "https://maven.terraformersmc.com/")
	}

	dependencies {
		val minecraft by configurations
		val mappings by configurations

		minecraft("com.mojang:minecraft:${minecraft_version}")
		mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
		implementation(annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.2")!!)
	}

	java.toolchain {
		languageVersion = JavaLanguageVersion.of("$java_version")
	}

	version = "$mod_version"
	group = "me.thosea"

	rootProject.tasks.build {
		dependsOn(tasks.build)
	}
}

subprojects {
	if(name == "common") return@subprojects

	tasks.processResources {
		val properties = mapOf("version" to project.version)
		inputs.properties(properties)

		filesMatching(listOf(
			"fabric.mod.json",
			"META-INF/mods.toml", "META-INF/neoforge.mods.toml"
		)) {
			expand(properties)
		}
	}

	val common by configurations.creating
	val shadowBundle by configurations.creating

	dependencies {
		common(project(":common", configuration = "namedElements")) {
			isTransitive = false
		}
	}

	tasks.shadowJar {
		configurations = setOf(shadowBundle)
		archiveClassifier = "dev-shadow"
	}

	with(configurations) {
		with(common) {
			isCanBeResolved = true
			isCanBeConsumed = false
		}
		compileClasspath.get().extendsFrom(common)
		runtimeClasspath.get().extendsFrom(common)

		// Files in this configuration will be bundled into your mod using the Shadow plugin.
		// Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
		with(shadowBundle) {
			isCanBeResolved = true
			isCanBeConsumed = false
		}
	}
}

rootProject.tasks.jar {
	enabled = false
}

val jarName = "BadOptimizations-${mod_version}-1.20.2-20.4.jar"

forgix {
	group = "me.thosea"
	mergedJarName = jarName
	outputDir = "build/libs"

	tasks.mergeJars { dependsOn(tasks.build) }
	tasks.build { finalizedBy(tasks.mergeJars) }
}

publishMods {
	file = file("build/libs/${jarName}")
	displayName = "$mod_version (1.20.2-1.20.4)"

	version = "$mod_version"
	type = STABLE
	modLoaders.add("fabric")
	modLoaders.add("neoforge")

	// tokens from HOME/.gradle/gradle.properties

	val mr_token by properties
	val cf_token by properties

	modrinth {
		accessToken = "$mr_token"
		projectId = "g96Z4WVZ"
		minecraftVersions.add("1.20.2")
		minecraftVersions.add("1.20.3")
		minecraftVersions.add("1.20.4")
	}

	curseforge {
		accessToken = "$cf_token"
		projectId = "949555"
		minecraftVersions.add("1.20.2")
		minecraftVersions.add("1.20.3")
		minecraftVersions.add("1.20.4")
		clientRequired = true
	}

	with(file("changelog.md")) {
		publishMods.changelog = readText().trim()
	}

	tasks.publishMods {
		dependsOn(tasks.mergeJars)
	}
}