package me.thosea.badoptimizations.interfaces;

import me.thosea.badoptimizations.utils.BadVersionHacks;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

@FunctionalInterface
public interface BiomeSkyColorGetter {
	int get(int biomeX, int biomeY, int biomeZ);

	static BiomeSkyColorGetter of(BiomeManager access) {
		return (x, y, z) -> {
			Biome biome = access.getNoiseBiomeAtQuart(x, y, z).value();
			return BadVersionHacks.getSkyColor(biome);
		};
	}
}