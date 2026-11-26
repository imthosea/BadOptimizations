package me.thosea.badoptimizations.interfaces;

import me.thosea.badoptimizations.utils.BadVersionHacks;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

public record BiomeSkyColorGetter(BiomeManager access) {
	public int get(int biomeX, int biomeY, int biomeZ) {
		Biome biome = access.getNoiseBiomeAtQuart(biomeX, biomeY, biomeZ).value();
		return BadVersionHacks.getSkyColor(biome);
	}
}