dependencies {
	// We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
	// Do NOT use other classes from fabric loader
	val ver = rootProject.property("fabric_loader_version")!!.toString()
	modImplementation("net.fabricmc:fabric-loader:$ver")
}

architectury {
	common("fabric", "neoforge") {
		platformPackage("neoforge", "forge")
	}
}