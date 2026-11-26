package me.thosea.badoptimizations.interfaces;

import net.minecraft.world.level.biome.BiomeManager;

public record BiomeSkyColorGetter(BiomeManager access) {
	public int get(int biomeX, int biomeY, int biomeZ) {
		return access.getNoiseBiomeAtQuart(biomeX, biomeY, biomeZ).value().getSkyColor();
	}
}