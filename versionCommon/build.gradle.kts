plugins {
	java
}

// this project is for stuff that doesn't change across versions
// that is, the config template TXT, the icon PNG
// & code for the config and PlatformMethods

repositories {
	mavenCentral()
}

dependencies {
	compileOnly(libs.slf4j)
	compileOnly(libs.annotations)
}

sourceSets.main {
	java.setSrcDirs(listOf("src"))
	resources.setSrcDirs(listOf("resources"))
}

java.toolchain {
	languageVersion.set(JavaLanguageVersion.of(17))
}