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
			modSourceSets.add(sourceSets.main)
			if(!obfuscated) {
				// needed so runClient picks up the mixin file
				modSourceSets.add(project(":common").sourceSets.main)
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
	val major = split[0].toInt()
	val minor = split[1].toInt()
	@Suppress("AvoidDuplicateDependencies")
	if(major > 21 || (major == 21 && minor >= 9)) {
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
}

// we need :setupMonoJar to get the obfuscated jar on 1.20.1
// or everything crashes!
configurations.register("forgejar")
artifacts {
	add("forgejar", if(legacy) {
		tasks.named("reobfJar")
	} else {
		tasks.named("jar")
	})
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

	val split = neoVersion.split(".")
	val oldToml = legacy || (split[0].toInt() <= 20 && split[1].toInt() <= 4) // 1.20.4-

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
			"maybe_legacy_mandatory_line" to if(oldToml) {
				"mandatory = true"
			} else {
				""
			},
		)
		rename { if(oldToml) "mods.toml" else "neoforge.mods.toml" }
	}
	from("../branch/accesstransformer.cfg") {
		into("META-INF")
	}

	if(obfuscated) {
		from("../branch/badoptimizations.mixins.json") {
			rename { "badoptimizations-forge.mixins.json" }
			if(legacy) filter { line ->
				if("\"refmap\"" in line) {
					"\"refmap\": \"badoptimizations-forge.mixins.refmap.json\","
				} else {
					line
				}
			}
		}
	}

	inputs.property("version", version)
	inputs.file("../branch/branch.versions.toml")
}

java.toolchain {
	languageVersion.set(JavaLanguageVersion.of(branch.versions.java.get()))
}