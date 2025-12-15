package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.config.Config;
import me.thosea.badoptimizations.hook.CacheHooks;
import me.thosea.badoptimizations.interfaces.BiomeSkyColorGetter;
import me.thosea.badoptimizations.utils.CommonColorFactors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

import static me.thosea.badoptimizations.utils.CommonColorFactors.lastLightningTicks;
import static me.thosea.badoptimizations.utils.CommonColorFactors.rainGradientMultiplier;
import static me.thosea.badoptimizations.utils.CommonColorFactors.thunderGradientMultiplier;

@Mixin(ClientLevel.class)
public abstract class MixinClientWorld extends Level {
	@Shadow @Final private Minecraft minecraft;

	private final BiomeSkyColorGetter bo$biomeColors = BiomeSkyColorGetter.of(getBiomeManager());
	private final CommonColorFactors bo$commonFactors = CommonColorFactors.SKY_COLOR;

	private Vec3 bo$skyColorCache;

	private int bo$lastBiomeColor = Integer.MIN_VALUE;
	private Vec3 bo$biomeColorVector = Vec3.ZERO;

	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	private void onGetSkyColor(Vec3 cameraPos, float tickDelta, CallbackInfoReturnable<Vec3> cir) {
		if(bo$skyColorCache == null || minecraft.player == null) return;

		CommonColorFactors.tick(tickDelta);

		if(this.bo$commonFactors.didTickChange()) {
			if(bo$isBiomeDirty(cameraPos.subtract(2.0, 2.0, 2.0).scale(0.25))) {
				bo$commonFactors.updateLastTime();
				// Do vanilla behavior, so surrounding biomes are factored in
				return;
			} else if(bo$commonFactors.isDirty() || bo$commonFactors.getTimeDelta() >= Config.skyColorTimeForUpdate || CacheHooks.invokeSkyColor()) {
				bo$skyColorCache = bo$calcSkyColor(tickDelta);
				bo$commonFactors.updateLastTime();
			}
		}

		cir.setReturnValue(bo$skyColorCache);
	}

	private boolean bo$isBiomeDirty(Vec3 pos) {
		int x = Mth.floor(pos.x);
		int y = Mth.floor(pos.y);
		int z = Mth.floor(pos.z);

		int color = bo$biomeColors.get(x - 2, y - 2, z - 2);
		if(bo$lastBiomeColor != color) {
			bo$lastBiomeColor = color;
			bo$biomeColorVector = Vec3.fromRGB24(color);
			return true;
		} else if(bo$biomeColors.get(x + 3, y + 3, z + 3) != color) {
			return true;
		}

		return false;
	}

	@Shadow public abstract int getSkyFlashTime();
	@Shadow public abstract Vec3 getSkyColor(Vec3 cameraPos, float tickDelta);

	private Vec3 bo$calcSkyColor(float delta) {
		float angle = Mth.cos(getTimeOfDay(1.0f) * 6.2831855F) * 2.0F + 0.5F;
		angle = Mth.clamp(angle, 0.0F, 1.0F);

		double x = bo$biomeColorVector.x * angle;
		double y = bo$biomeColorVector.y * angle;
		double z = bo$biomeColorVector.z * angle;

		if(rainGradientMultiplier > 0.0f) {
			double color = (x * 0.3F + y * 0.59F + z * 0.11F) * 0.6F;

			x = x * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
			y = y * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
			z = z * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
		}
		if(thunderGradientMultiplier > 0.0f) {
			double color = (x * 0.3F + y * 0.59F + z * 0.11F) * 0.2F;

			x = x * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
			y = y * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
			z = z * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
		}
		if(lastLightningTicks > 0) {
			float lightningMultiplier = lastLightningTicks - delta;
			if(lightningMultiplier > 1.0F) {
				lightningMultiplier = 1.0F;
			}

			lightningMultiplier *= 0.45F;
			x = x * (1.0F - lightningMultiplier) + 0.8F * lightningMultiplier;
			y = y * (1.0F - lightningMultiplier) + 0.8F * lightningMultiplier;
			z = z * (1.0F - lightningMultiplier) + lightningMultiplier;
		}

		return new Vec3(x, y, z);
	}

	@Inject(method = "getSkyColor", at = @At("RETURN"))
	private void afterGetSkyColor(Vec3 cameraPos, float tickDelta, CallbackInfoReturnable<Vec3> cir) {
		bo$skyColorCache = cir.getReturnValue();
	}

	protected MixinClientWorld(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
	}
}