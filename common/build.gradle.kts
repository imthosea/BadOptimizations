dependencies {
	// We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
	// Do NOT use other classes from fabric loader
	val ver = rootProject.property("fabric_loader_version")!!.toString()
	implementation("net.fabricmc:fabric-loader:$ver")
}

architectury {
	common("fabric", "neoforge") {
		platformPackage("neoforge", "forge")
	}
}

loom {
	// since its only used from mixin, we dont actually need to mark this
	// in fabric.mod.json or for neoforge
	accessWidenerPath = file("src/main/resources/badoptimizations.accesswidener")
}