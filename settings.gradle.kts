pluginManagement {
	repositories {
		maven(url = "https://maven.fabricmc.net/")
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	versionCatalogs {
		create("branch") {
			from(files("branch/branch.versions.toml"))
		}
	}
}

//@Suppress("UnstableApiUsage") // it's like jetpack compose, everything is @Incubating
rootProject.name = "badoptimizations"

include(":versionCommon")
include(":common")
include(":fabric")
include(":forge")