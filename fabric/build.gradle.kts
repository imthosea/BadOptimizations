@file:Suppress("LocalVariableName")

architectury {
	platformSetupLoomIde()
	fabric()
}

with(configurations) {
	get("developmentFabric").extendsFrom(get("common"))
}

val shadowBundle by configurations

dependencies {
	val fabric_loader_version by rootProject.properties
	val fabric_modmenu_version by rootProject.properties
	modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
	modImplementation("com.terraformersmc:modmenu:${fabric_modmenu_version}")

	shadowBundle(project(":common", configuration = "transformProductionFabric"))

	// Uncomment in < 1.20.2
	include(implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.2.2")!!)!!)
}

tasks.remapJar {
	inputFile = tasks.shadowJar.get().archiveFile
}