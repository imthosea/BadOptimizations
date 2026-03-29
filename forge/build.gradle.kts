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

dependencies {
	val neo_version by rootProject.properties
	neoForge("net.neoforged:neoforge:$neo_version")

	shadowBundle(project(":common", configuration = "transformProductionNeoForge"))
}