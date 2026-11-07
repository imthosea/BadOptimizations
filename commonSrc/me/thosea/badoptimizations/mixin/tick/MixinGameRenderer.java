package me.thosea.badoptimizations.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public final class MixinGameRenderer {
	@Shadow @Final private Minecraft minecraft;

	// don't do unneeded FOV calculations
	@WrapOperation(method = "tickFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getFieldOfViewModifier(ZF)F"))
	private float getPlayerFov(AbstractClientPlayer player,
	                           boolean firstPerson, float fovEffectScale,
	                           Operation<Float> original) {
		if(fovEffectScale == 0f) {
			if(minecraft.options.getCameraType() == CameraType.FIRST_PERSON && player.isScoping()) {
				return 0.1f;
			} else {
				return 1.0f;
			}
		} else {
			return original.call(player, firstPerson, fovEffectScale);
		}
	}
}