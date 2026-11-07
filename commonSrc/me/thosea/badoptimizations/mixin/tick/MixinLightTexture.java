package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.config.Config;
import me.thosea.badoptimizations.hook.CacheHooks;
import me.thosea.badoptimizations.mixin.accessors.GameRendererAccessor;
import me.thosea.badoptimizations.mixin.accessors.PlayerAccessor;
import me.thosea.badoptimizations.utils.CommonColorFactors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public abstract class MixinLightTexture {
	@Shadow @Final private Minecraft minecraft;

	private final CommonColorFactors bo$commonFactors = CommonColorFactors.LIGHTMAP;

	private double bo$lastGamma;
	private DimensionSpecialEffects bo$lastDimension;
	private boolean bo$lastNightVision;
	private boolean bo$lastConduitPower;

	private float bo$previousSkyDarkness;
	private GameRendererAccessor bo$gameRendererAccessor;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(GameRenderer renderer, Minecraft client, CallbackInfo ci) {
		this.bo$gameRendererAccessor = (GameRendererAccessor) renderer;
	}

	private boolean bo$isDirty() {
		if(bo$commonFactors.getTimeDelta() >= Config.lightmapTimeForUpdate)
			return true;
		if(minecraft.player.isUnderWater() && ((PlayerAccessor) minecraft.player).bo$underwaterVisibilityTicks() < 600)
			return true; // water light fading

		MobEffectInstance nightVision = minecraft.player.getEffect(MobEffects.NIGHT_VISION);
		boolean hasNightVision = nightVision != null;
		if(bo$lastNightVision != hasNightVision) {
			bo$lastNightVision = hasNightVision;
			return true;
		} else if(nightVision != null && nightVision.endsWithin(200))
			return true; // flicker effect
		else if(minecraft.player.hasEffect(MobEffects.DARKNESS))
			return true; // flicker effect

		// Stuff that doesn't change as often

		boolean conduitPower = minecraft.player.hasEffect(MobEffects.CONDUIT_POWER);
		if(bo$lastConduitPower != conduitPower) {
			bo$lastConduitPower = conduitPower;
			return true;
		}
		DimensionSpecialEffects dimension = minecraft.level.effects();
		if(bo$lastDimension != dimension) {
			bo$lastDimension = dimension;
			return true;
		}
		float skyDarkness = bo$gameRendererAccessor.bo$getSkyDarkness();
		if(bo$previousSkyDarkness != skyDarkness) {
			bo$previousSkyDarkness = skyDarkness;
			return true;
		}
		double gamma = minecraft.options.gamma().get();
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
		if(minecraft.player == null) return;

		CommonColorFactors.tick(minecraft.getFrameTime());

		if(bo$commonFactors.didTickChange() && (bo$commonFactors.isDirty()) | this.bo$isDirty()) {
			bo$commonFactors.updateLastTime();
		} else {
			ci.cancel();
		}
	}
}