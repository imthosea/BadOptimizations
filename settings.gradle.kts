pluginManagement {
	repositories {
		maven(url = "https://maven.fabricmc.net/")
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	versionCatalogs {
		create("branch") {
			val file = file("branch/branch.versions.toml")
			check(file.exists()) {
				"""
				make sure to symlink the version you want to versions/current
				(type ln -sfn <version> versions/current>)
				""".trimIndent()
			}
			from(files(file))
		}
	}
}

rootProject.name = "badoptimizations"

include(":versionCommon")
include(":common")
include(":fabric")
include(":forge")