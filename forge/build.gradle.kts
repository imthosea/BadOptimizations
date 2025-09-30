@file:Suppress("LocalVariableName")

architectury {
	platformSetupLoomIde()
	neoForge {
		platformPackage = "forge"
	}
}

with(configurations) {
	get("developmentNeoForge").extendsFrom(get("common"))
}

repositories {
	maven(url = "https://maven.neoforged.net/releases/")
}

val shadowBundle by configurations

dependencies {
	val neo_version by rootProject.properties
	neoForge("net.neoforged:neoforge:$neo_version")

	shadowBundle(project(":common", configuration = "transformProductionNeoForge"))

	// Uncomment in < 1.20.2
//	implementation(include("io.github.llamalad7:mixinextras-forge:0.3.2")!!)
}

tasks.remapJar {
	inputFile = tasks.shadowJar.get().archiveFile
}