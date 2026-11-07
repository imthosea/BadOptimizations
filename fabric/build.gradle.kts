import java.util.*

plugins {
	java
	val obfuscated = branch.versions.isObfuscated.get().toBooleanStrict()
	if(!obfuscated) {
		alias(libs.plugins.loom)
	} else {
		alias(libs.plugins.loom.legacy)
	}
}

repositories {
	mavenCentral()
	maven(url = "https://maven.terraformersmc.com/") {
		content {
			includeGroup("com.terraformersmc")
		}
	}
}

val obfuscated = branch.versions.isObfuscated.get().toBooleanStrict()

dependencies {
	minecraft(branch.fabric.minecraft)
	implementation(project(":versionCommon"))

	@Suppress("AvoidDuplicateDependencies")
	if(obfuscated) {
		configurations.named("mappings")(loom.officialMojangMappings())
		configurations.named("modImplementation")(branch.fabric.loader)
		configurations.named("modCompileOnly")(branch.fabric.modmenu) {
			// we don't need minecraft (again)
			isTransitive = false
		}
	} else {
		implementation(project(":common"))
		implementation(branch.fabric.loader)
		compileOnly(branch.fabric.modmenu) {
			isTransitive = false
		}
	}

	if(branch.versions.fabricLoader.get().split('.')[1].toInt() < 15) {
		implementation(annotationProcessor(libs.mixinextras.common.get())!!)
		runtimeOnly(include(libs.mixinextras.fabric.get())!!)
	}
}

loom {
	accessWidenerPath = file("../branch/badoptimizations.accesswidener")
}

sourceSets.main {
	java.setSrcDirs(if(obfuscated) {
		listOf("../branch/fabricSrc", "../branch/commonSrc")
	} else {
		listOf("../branch/fabricSrc")
	})
	resources.setSrcDirs(Collections.EMPTY_LIST)
}
tasks.processResources {
	val version = version
	val mcRange = branch.versions.fabricMinecraftRange.get()
	val fabricLoaderVer = branch.versions.fabricLoader.get()
	from("fabric.mod.json") {
		expand(
			"version" to version,
			"fabric_minecraft_range" to mcRange,
			"fabric_loader_ver" to fabricLoaderVer,
			"fabric_mixin_file" to if(obfuscated) {
				"badoptimizations-fabric.mixins.json"
			} else {
				"badoptimizations.mixins.json"
			},
		)
	}
	from("../branch/badoptimizations.accesswidener")

	if(obfuscated) {
		from("../branch/badoptimizations.mixins.json") {
			rename { "badoptimizations-fabric.mixins.json" }
		}
	}

	inputs.file("../branch/branch.versions.toml")
}

java.toolchain {
	languageVersion.set(JavaLanguageVersion.of(branch.versions.java.get()))
}