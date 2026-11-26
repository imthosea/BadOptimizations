import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper

buildscript {
	dependencies {
		classpath(libs.build.asm)
	}
}

plugins {
	idea
	alias(libs.plugins.publish.plugin)
	// stops weird casting errors
	alias(libs.plugins.loom) apply false
	alias(libs.plugins.loom.legacy) apply false
	alias(libs.plugins.moddevgradle) apply false
	alias(libs.plugins.moddevgradle.legacy) apply false
}

idea {
	module {
		// come join the army of kotlin
		excludeDirs.addAll(listOf(
			"versions",
			"fabric/run", "forge/run",
		).map(::file))
		isDownloadSources = true
		isDownloadJavadoc = true
		name = branch.versions.minecraft.get()
	}
}

val common = configurations.register("common")
val versionCommon = configurations.register("versionCommon")
val fabric = configurations.register("fabric")
val forge = configurations.register("forge")
dependencies {
	common(project(":common")) { isTransitive = false }
	versionCommon(project(":versionCommon")) { isTransitive = false }
	fabric(project(":fabric")) { isTransitive = false }
	forge(project(":forge", configuration = "forgejar")) { isTransitive = false }
}

/*
 * sadly we can't do all of this within a single jar copy task,
 * because we can't modify files freely when they're being copied
 * we can "filter" the content
 * https://docs.gradle.org/current/userguide/working_with_files.html#sec:filtering_files
 * which works great to modify mixin files
 * but it's hardcoded to run through a text charset first, so it ends up corrupting class files
 */
/*
 * so instead now we have one task that setups everything into some build directory,
 * and then just walks through the directory in a doLast to remap the class files
 * then below there's a super simple archive task that makes a jar out of that folder
 * works well enough
 */
val setupMonoJar = tasks.register<Sync>("setupMonoJar") {
	description = "fabric+forge=???"

	dependsOn(common, versionCommon, fabric, forge)
	into(layout.buildDirectory.dir("monojar"))
	includeEmptyDirs = false
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE

	val commonJar = common.map { zipTree(it.singleFile) }
	val versionCommonJar = versionCommon.map { zipTree(it.singleFile) }
	val fabricJar = fabric.map { zipTree(it.singleFile) }
	val forgeJar = forge.map { zipTree(it.singleFile) }
	val obfuscated = branch.versions.isObfuscated.get().toBooleanStrict()

	fun remap(name: String, project: String): String {
		val noRemap = arrayOf(
			"me/thosea/badoptimizations/config/",
			"me/thosea/badoptimizations/hook/",
			"me/thosea/badoptimizations/platform/",
			"me/thosea/badoptimizations/reflect/",
			"me/thosea/badoptimizations/${project}/"
		)
		return if(noRemap.any { name.startsWith(it) }) {
			name
		} else {
			name.replaceFirst("me/thosea/badoptimizations/", "me/thosea/badoptimizations/${project}/")
		}
	}

	if(obfuscated) {
		from(versionCommonJar) {
			exclude("META-INF/MANIFEST.MF")
		}
		from(fabricJar) {
			exclude("META-INF/MANIFEST.MF")
			filesMatching("**/*.class") {
				path = remap(path, "fabric")
			}
			filesMatching("badoptimizations-fabric.mixins.json") {
				filter {
					it.replace("me.thosea.badoptimizations.mixin", "me.thosea.badoptimizations.fabric.mixin")
				}
			}
		}
		from(forgeJar) {
			exclude("META-INF/MANIFEST.MF")
			filesMatching("**/*.class") {
				path = remap(path, "forge")
			}
			filesMatching("badoptimizations-forge.mixins.json") {
				filter {
					it.replace("me.thosea.badoptimizations.mixin", "me.thosea.badoptimizations.forge.mixin")
				}
			}
			filesMatching("badoptimizations-forge.mixins.refmap.json") {
				// (only in 1.20.1)
				filter {
					it.replace(
						"me/thosea/badoptimizations/mixin/",
						"me/thosea/badoptimizations/forge/mixin/"
					)
				}
			}
		}
	} else {
		from(commonJar)
		from(versionCommonJar)
		from(fabricJar)
		from(forgeJar)
	}

	doLast {
		if(!obfuscated) return@doLast
		println("doing remapping")
		fun remapClass(file: File, platform: String) {
			val writer = ClassWriter(0)
			val reader = ClassReader(file.readBytes())
			val remapper = ClassRemapper(writer, object: Remapper(Opcodes.ASM9) {
				override fun map(name: String) = remap(name, platform)
			})
			reader.accept(remapper, ClassReader.EXPAND_FRAMES)
			file.writeBytes(writer.toByteArray())
		}
		destinationDir.resolve("me/thosea/badoptimizations/fabric")
			.walk()
			.filter { it.isFile }
			.forEach { remapClass(it, "fabric") }
		destinationDir.resolve("me/thosea/badoptimizations/forge")
			.walk()
			.filter { it.isFile }
			.forEach { remapClass(it, "forge") }
	}
}
val assembleMonoJar = tasks.register<Jar>("assembleMonoJar") {
	// basename-appendix-version-classifier.jar
	archiveBaseName = "BadOptimizations"
	archiveAppendix = version.toString()
	archiveVersion = branch.versions.jarTitle
	destinationDirectory = layout.buildDirectory
	dependsOn(setupMonoJar)
	from(setupMonoJar.map { it.destinationDir })

	if(branch.versions.minecraft.get() == "1.20.1") {
		// 1.20.1 forge puts mixin stuff in MANIFEST.MF
		// who knows why?
		manifest.attributes("MixinConfigs" to "badoptimizations-forge.mixins.json")
	}
}

tasks.register<Delete>("clean") {
	delete(layout.buildDirectory)
}

interface InjectedStuff {
	@get:Inject
	val exec: ExecOperations
}
tasks.register("testPrism") {
	doNotTrackState("no")
	dependsOn(assembleMonoJar)

	val prismInstancesDir = providers.gradleProperty("bo.prismInstancesDir").map(::File)
	val prismBin = providers.gradleProperty("bo.prismBin")
	val prismArgs = providers.gradleProperty("bo.prismArgs").orElse("")
	val prismBaseName = providers.gradleProperty("bo.prismBaseName").orElse("bo_")
	val prismSequential = providers.gradleProperty("bo.prismSequential").map(String::toBooleanStrict)
	val prismTests = branch.versions.prismTests.map { it.split(",") }

	val inject = objects.newInstance<InjectedStuff>()
	val jar = assembleMonoJar.flatMap { it.archiveFile }

	doLast {
		val prismInstancesDir = prismInstancesDir.get()
		val prismBaseName = prismBaseName.get()
		val prismTests = prismTests.get()
		// seems like the prism CLI doesn't report when there's a crash
		// so as hack we just check if a new crash file was added
		val crashChecks = ArrayDeque<() -> Unit>()

		for(test in prismTests) {
			val dir = prismInstancesDir.resolve("$prismBaseName$test/minecraft")
			check(dir.isDirectory) { "Missing dir: $dir" }
			val mod = dir.resolve("mods/BadOptimizations.jar")
			mod.parentFile.mkdirs()
			jar.get().asFile.copyTo(mod, overwrite = true)

			val crashFolder = dir.resolve("crash-reports").takeIf(File::isDirectory)
			val crashFiles = crashFolder?.list() ?: emptyArray()
			val hsErrPids = dir.list()!!.filter { it.startsWith("hs_err_pid") }
			crashChecks.add {
				val newCrashFiles = crashFolder?.list() ?: emptyArray()
				val newHsErrPids = dir.list()?.filter { it.startsWith("hs_err_pid") }
				check(crashFiles.contentEquals(newCrashFiles) && hsErrPids == newHsErrPids) {
					"Prism instance $test crashed"
				}
			}
		}

		val tasks = mutableListOf<Thread>()
		for(test in prismTests) { // after we made sure they all exist
			val task = Runnable {
				inject.exec.exec {
					commandLine(
						prismBin.get(),
						*prismArgs.get().split(" ").toTypedArray(),
						"--launch", "$prismBaseName$test"
					)
				}.assertNormalExitValue()
			}
			if(prismSequential.get()) {
				task.run()
				crashChecks.removeFirst().invoke()
			} else {
				// good luck
				tasks.add(Thread.startVirtualThread(task))
			}
		}
		if(!prismSequential.get()) {
			tasks.forEach(Thread::join)
			crashChecks.forEach { it.invoke() }
		}
		println("Congrats")
	}
}

publishMods {
	displayName = "${project.version} ${branch.versions.publishTitle.get()}"
	type = if(providers.gradleProperty("isReleaseStable").get().toBooleanStrict()) {
		this.STABLE
	} else {
		this.BETA
	}

	file.set(assembleMonoJar.flatMap { it.archiveFile })
	changelog = file("changelog.md").let { provider { it.readText() } }

	val versions = branch.versions.publishVersions.map { it.split(",") }
	val loaders = branch.versions.publishLoaders.map { it.split(",") }

	modLoaders.set(loaders)

	curseforge {
		accessToken = providers.gradleProperty("bo.cfToken")
		projectId = "949555"
		minecraftVersions.set(versions)
		client = true
		server = false
	}
	modrinth {
		accessToken = providers.gradleProperty("bo.mrToken")
		projectId = "g96Z4WVZ"
		environment = CLIENT_ONLY
		minecraftVersions.set(versions)
	}
}