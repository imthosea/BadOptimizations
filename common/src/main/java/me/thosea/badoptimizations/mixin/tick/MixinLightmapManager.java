package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.config.Config;
import me.thosea.badoptimizations.hook.CacheHooks;
import me.thosea.badoptimizations.mixin.accessors.GameRendererAccessor;
import me.thosea.badoptimizations.mixin.accessors.PlayerAccessor;
import me.thosea.badoptimizations.utils.CommonColorFactors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapManager {
	@Shadow @Final private MinecraftClient client;

	private final CommonColorFactors bo$commonFactors = CommonColorFactors.LIGHTMAP;

	private double bo$lastGamma;
	private DimensionEffects bo$lastDimension;
	private boolean bo$lastNightVision;
	private boolean bo$lastConduitPower;

	private float bo$previousSkyDarkness;
	@Final private GameRendererAccessor bo$gameRendererAccessor;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(GameRenderer renderer, MinecraftClient client, CallbackInfo ci) {
		this.bo$gameRendererAccessor = (GameRendererAccessor) renderer;
	}

	private boolean bo$isDirty() {
		if(bo$commonFactors.getTimeDelta() >= Config.lightmapTimeForUpdate)
			return true;
		if(client.player.isSubmergedInWater() && ((PlayerAccessor) client.player).bo$underwaterVisibilityTicks() < 600)
			return true; // water light fading

		StatusEffectInstance nightVision = client.player.getStatusEffect(StatusEffects.NIGHT_VISION);
		boolean hasNightVision = nightVision != null;
		if(bo$lastNightVision != hasNightVision) {
			bo$lastNightVision = hasNightVision;
			return true;
		} else if(nightVision != null && nightVision.isDurationBelow(200))
			return true; // flicker effect
		else if(client.player.hasStatusEffect(StatusEffects.DARKNESS))
			return true; // flicker effect

		// Stuff that doesn't change as often

		boolean conduitPower = client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER);
		if(bo$lastConduitPower != conduitPower) {
			bo$lastConduitPower = conduitPower;
			return true;
		}
		DimensionEffects dimension = client.world.getDimensionEffects();
		if(bo$lastDimension != dimension) {
			bo$lastDimension = dimension;
			return true;
		}
		float skyDarkness = bo$gameRendererAccessor.bo$getSkyDarkness();
		if(bo$previousSkyDarkness != skyDarkness) {
			bo$previousSkyDarkness = skyDarkness;
			return true;
		}
		double gamma = client.options.getGamma().getValue();
		if(bo$lastGamma != gamma) { // jamma celestial??
			bo$lastGamma = gamma;
			return true;
		}
		if(CacheHooks.invokeLightmap()) {
			return true;
		}
		return false;
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		if(client.player == null) return;

		CommonColorFactors.tick(client.getTickDelta());

		if(bo$commonFactors.didTickChange() && (bo$commonFactors.isDirty()) | this.bo$isDirty()) {
			bo$commonFactors.updateLastTime();
		} else {
			ci.cancel();
		}
	}
}