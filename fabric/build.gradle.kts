@file:Suppress("LocalVariableName")

architectury {
	platformSetupLoomIde()
	fabric()
}

with(configurations) {
	get("developmentFabric").extendsFrom(get("common"))
}

dependencies {
	val fabric_loader_version by rootProject.properties
	val fabric_modmenu_version by rootProject.properties
	implementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
	compileOnly("com.terraformersmc:modmenu:${fabric_modmenu_version}")
	shadowBundle(project(":common", configuration = "transformProductionFabric"))
}