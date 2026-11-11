package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.hook.CacheHooks;
import me.thosea.badoptimizations.mixin.accessors.GameRendererAccessor;
import me.thosea.badoptimizations.mixin.accessors.PlayerAccessor;
import me.thosea.badoptimizations.reflect.BadReflection;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EndFlashState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class MixinLightmapExtractor {
	// for 26.1 - 26.3 compat
	private static final MethodHandle bo$getCamera = BadReflection.getVirtual(
			GameRenderer.class,
			MethodType.methodType(Camera.class),
			"mainCamera", // 26.1
			"getMainCamera" // 26.2+
	);
	// 26.1/2-EnvironmentAttribute<Integer>
	// 26.3-EnvironmentAttribute<Vector3fc>
	private static final boolean bo$IS_SKY_COLOR_VEC =
			((EnvironmentAttribute<?>) EnvironmentAttributes.SKY_LIGHT_COLOR)
					.defaultValue() instanceof Vector3fc;

	@SuppressWarnings("UNCHECKED_CAST")
	private static <T> T bo$cast(Object obj) {
		return (T) obj;
	}

	@Shadow @Final private Minecraft minecraft;

	private EnvironmentAttributeProbe bo$probe;
	private GameRendererAccessor bo$gameRendererAccessor;

	// 26.1/26.2
	private int bo$lastSkyColor;
	// 26.3
	// darnit, valhalla is so close...
	private float bo$lastSkyColorR;
	private float bo$lastSkyColorG;
	private float bo$lastSkyColorB;

	private float bo$lastSkyFactor;

	private float bo$lastEndFactor = 0f;
	private double bo$lastGamma;
	private DimensionType bo$lastDimension;
	private boolean bo$lastNightVision;
	private boolean bo$lastConduitPower;

	private float bo$previousSkyDarkness;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(GameRenderer renderer, Minecraft client, CallbackInfo ci) {
		this.bo$gameRendererAccessor = (GameRendererAccessor) renderer;
		try {
			this.bo$probe = ((Camera) bo$getCamera.invokeExact(renderer)).attributeProbe();
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		if(minecraft.player == null) return;

		if(!this.bo$isDirty()) {
			ci.cancel();
		}
	}

	private boolean bo$isDirty() {
		float skyFactor = bo$probe.getValue(EnvironmentAttributes.SKY_LIGHT_FACTOR, 1.0f);
		if(bo$IS_SKY_COLOR_VEC) {
			Vector3fc skyColor = bo$probe.getValue(EnvironmentAttributes.SKY_LIGHT_COLOR, 1.0f);
			if(
				// stupid formatter
				/*  */ bo$lastSkyColorR != skyColor.x()
					|| bo$lastSkyColorG != skyColor.y()
					|| bo$lastSkyColorB != skyColor.z()
					|| bo$lastSkyFactor != skyFactor
			) {
				this.bo$lastSkyColorR = skyColor.x();
				this.bo$lastSkyColorG = skyColor.y();
				this.bo$lastSkyColorB = skyColor.z();
				this.bo$lastSkyFactor = skyFactor;
				return true;
			}
		} else {
			int skyColor = (int) bo$probe.getValue((EnvironmentAttribute<?>) EnvironmentAttributes.SKY_LIGHT_COLOR, 1.0f);
			if(bo$lastSkyColor != skyColor || bo$lastSkyFactor != skyFactor) {
				this.bo$lastSkyColor = skyColor;
				this.bo$lastSkyFactor = skyFactor;
				return true;
			}
		}

		if(minecraft.player.isUnderWater() && ((PlayerAccessor) minecraft.player).bo$underwaterVisibilityTicks() < 600)
			return true; // water light fading

		if(!minecraft.options.hideLightningFlash().get()) {
			EndFlashState flash = minecraft.level.endFlashState();
			if(flash != null) {
				float factor = flash.getIntensity(minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false));
				if(this.bo$lastEndFactor != factor) {
					this.bo$lastEndFactor = factor;
					return true;
				}
			}
		}

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
		DimensionType dimension = minecraft.level.dimensionType();
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
		if(CacheHooks.invokeCommon() || CacheHooks.invokeLightmap()) {
			return true;
		}
		return false;
	}
}