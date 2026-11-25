package me.thosea.badoptimizations.mixin.debugrender;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BeeDebugRenderer.class)
public class MixinBeeDebugRenderer {
	@Shadow @Final private Map<?, ?> hives; // <BlockPos, HiveDebugInfo>
	@Shadow @Final private Map<?, ?> beeInfosPerEntity; // <UUID, BeeDebugPayload.BeeInfo>

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;DDD)V", at = @At("HEAD"), cancellable = true)
	private void onRender(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if(hives.isEmpty() && beeInfosPerEntity.isEmpty())
			ci.cancel();
	}
}