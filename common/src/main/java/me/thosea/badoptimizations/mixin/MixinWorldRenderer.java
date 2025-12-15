package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LevelRenderer.class, priority = 700)
public abstract class MixinWorldRenderer {
	// method_62215 = lambda in renderSky
	@WrapOperation(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSunAngle(F)F"))
	private float cacheSkyAngleRadians(ClientLevel world, float delta, Operation<Float> original,
	                                   @Share("skyAngleRadians") LocalFloatRef skyAngleRadians) {
		float result = original.call(world, delta);
		skyAngleRadians.set(result);
		return result;
	}

	@WrapOperation(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"))
	private float getSkyAngle(ClientLevel instance, float delta, Operation<Float> original,
	                          @Share("skyAngleRadians") LocalFloatRef skyAngleRadians) {
		return skyAngleRadians.get() / 6.2831855F;
	}
}