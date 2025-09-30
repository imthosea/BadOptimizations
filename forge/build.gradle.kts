@file:Suppress("LocalVariableName")

architectury {
	platformSetupLoomIde()
	forge()
}

with(configurations) {
	get("developmentForge").extendsFrom(get("common"))
}

val shadowBundle by configurations

dependencies {
	val forge_version by rootProject.properties
	forge("net.minecraftforge:forge:$forge_version")

	shadowBundle(project(":common", configuration = "transformProductionForge"))

	// Uncomment in < 1.20.2
	implementation(include("io.github.llamalad7:mixinextras-forge:0.3.2")!!)
}

tasks.remapJar {
	inputFile = tasks.shadowJar.get().archiveFile
}

loom.forge.mixinConfig("badoptimizations.mixins.json")