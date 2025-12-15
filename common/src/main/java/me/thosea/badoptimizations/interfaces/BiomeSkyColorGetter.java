package me.thosea.badoptimizations.interfaces;

import net.minecraft.world.level.biome.BiomeManager;

@FunctionalInterface
public interface BiomeSkyColorGetter {
	int get(int biomeX, int biomeY, int biomeZ);

	static BiomeSkyColorGetter of(BiomeManager access) {
		return (x, y, z) -> {
			return access.getNoiseBiomeAtQuart(x, y, z).value().getSkyColor();
		};
	}
}