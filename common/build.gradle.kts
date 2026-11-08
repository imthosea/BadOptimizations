import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension
import java.util.*

plugins {
	java
	/*
	 * loom also works for the common project, but it puts in EnvType stuff,
	 * which makes the forge compile task nag warnings about unknown enum constants
	 * thankfully moddevgradle has this "vanilla mode"
	 * https://github.com/neoforged/ModDevGradle#vanilla-mode
	 */
	if(branch.versions.minecraft.get() != "1.20.1") {
		alias(libs.plugins.moddevgradle)
	} else {
		alias(libs.plugins.moddevgradle.legacy)
	}
}

val obfuscated = branch.versions.isObfuscated.get().toBooleanStrict()
tasks.compileJava {
	/*
	 * obfuscated releases use different mappings on fabric/forge,
	 * so for those, the common project is really only for IDE stuff,
	 * and then the fabric/forge project includes commonSrc as yet another source set
	 * unobfuscated releases: we can just link against the common project
	 * and compile normally
	 */
	enabled = !obfuscated
}

val access = rootProject.file("branch/accesstransformer.cfg")
if(branch.versions.minecraft.get() != "1.20.1") {
	extensions.configure<NeoForgeExtension> {
		neoFormVersion = branch.versions.neoform.get()
		validateAccessTransformers = true
		accessTransformers.from(access)
	}
} else {
	extensions.configure<LegacyForgeExtension> {
		mcpVersion = branch.versions.minecraft.get()
		validateAccessTransformers = true
		accessTransformers.from(access)
	}
}

dependencies {
	compileOnly(libs.mixin)
	compileOnly(libs.mixinextras.common)
	implementation(project(":versionCommon"))
}

sourceSets.main {
	java.setSrcDirs(listOf("../branch/commonSrc"))
	resources.setSrcDirs(Collections.EMPTY_LIST)
}
tasks.processResources {
	enabled = !obfuscated
	from("../branch/badoptimizations.mixins.json")
}

java.toolchain {
	languageVersion.set(JavaLanguageVersion.of(branch.versions.java.get()))
}