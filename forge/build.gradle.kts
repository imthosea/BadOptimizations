import net.neoforged.moddevgradle.dsl.ModDevExtension
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension
import net.neoforged.moddevgradle.legacyforge.dsl.MixinExtension
import java.util.*

plugins {
	java
	if(branch.versions.minecraft.get() != "1.20.1") {
		alias(libs.plugins.moddevgradle)
	} else {
		alias(libs.plugins.moddevgradle.legacy)
	}
}

// https://github.com/neoforged/ModDevGradle/blob/main/LEGACY.md

val legacy = branch.versions.minecraft.get() == "1.20.1"
val obfuscated = branch.versions.isObfuscated.get().toBooleanStrict()

extensions.configure<ModDevExtension> {
	runs {
		register("client") {
			client()
		}
	}
	mods {
		register("badoptimizations") {
			sourceSet(sourceSets.main.get())
			if(!obfuscated) {
				// cursed, but needed so runClient picks up the mixin file
				sourceSet(project(":common").sourceSets.main.get())
			}
		}
	}
	accessTransformers.from("../branch/accesstransformer.cfg")
}
// the one time I'd want groovy...
if(legacy) {
	extensions.configure<LegacyForgeExtension> {
		version = branch.versions.neoforge.get()
	}
} else {
	extensions.configure<NeoForgeExtension> {
		version = branch.versions.neoforge.get()
	}
}

dependencies {
	val split = branch.versions.neoforge.get().split(".")
	@Suppress("AvoidDuplicateDependencies")
	if(split[0].toInt() >= 21 && split[0].toInt() >= 9) {
		implementation(project(":versionCommon"))
	} else {
		"additionalRuntimeClasspath"(implementation(project(":versionCommon"))!!)
	}
	if(!obfuscated) {
		implementation(project(":common"))
	}
	if(legacy) {
		annotationProcessor(libs.mixin)
		implementation(annotationProcessor(libs.mixinextras.common.get())!!)
		runtimeOnly(jarJar(libs.mixinextras.forge.get())!!)
	}
}

if(legacy) {
	extensions.configure<MixinExtension> {
		config("badoptimizations-forge.mixins.json")
		add(sourceSets.main.get(), "badoptimizations-forge.mixins.refmap.json")
	}
	// (manifest entries are set in :assembleMonoJar)

	// usually this extends from runtimeElements
	// but, in forge 1.20.1, this results in :setupMonoJar getting the unobfuscated jar
	// and then everything crashes
	configurations.named("default") {
		// where's type inference when you need it??
		setExtendsFrom(emptyList<Configuration>())
	}
	artifacts {
		add("default", tasks.named("reobfJar"))
	}
}

sourceSets.main {
	java.setSrcDirs(if(obfuscated) {
		listOf("../branch/forgeSrc", "../branch/commonSrc")
	} else {
		listOf("../branch/forgeSrc")
	})
	resources.setSrcDirs(Collections.EMPTY_LIST)
}
tasks.processResources {
	val version = version
	val legacy = legacy
	val neoVersion = branch.versions.neoforge.get()
	val range = branch.versions.forgeMinecraftRange.get()
	from("pack.mcmeta")
	from("mods.toml") {
		into("META-INF")
		expand(
			"version" to version,
			"forge_minecraft_range" to range,
			"forge_mixin_file" to if(obfuscated) {
				"badoptimizations-forge.mixins.json"
			} else {
				"badoptimizations.mixins.json"
			},
		)
		rename {
			val split = neoVersion.split(".")
			if(split[0].toInt() <= 20 && split[1].toInt() <= 4) { // 1.20.4-
				"mods.toml"
			} else {
				"neoforge.mods.toml"
			}
		}
	}
	from("../branch/accesstransformer.cfg") {
		into("META-INF")
	}

	if(obfuscated) {
		from("../branch/badoptimizations.mixins.json") {
			rename { "badoptimizations-forge.mixins.json" }
			if(legacy) filter {
				if("\"refmap\"" in it) {
					"\"refmap\": \"badoptimizations-forge.mixins.refmap.json\","
				} else {
					it
				}
			}
		}
	}

	inputs.file("../branch/branch.versions.toml")
}

java.toolchain {
	languageVersion.set(JavaLanguageVersion.of(branch.versions.java.get()))
}