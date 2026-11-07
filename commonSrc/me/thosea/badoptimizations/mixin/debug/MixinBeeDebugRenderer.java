package me.thosea.badoptimizations.mixin.debug;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.BeeDebugRenderer.BeeInfo;
import net.minecraft.core.BlockPos;

@Mixin(BeeDebugRenderer.class)
public class MixinBeeDebugRenderer {
	@Shadow @Final private Map<BlockPos, ?> hives; // i dont wanna use accesswidener
	@Shadow @Final private Map<UUID, BeeInfo> beeInfosPerEntity;

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;DDD)V", at = @At("HEAD"), cancellable = true)
	private void onRender(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if(hives.isEmpty() && beeInfosPerEntity.isEmpty())
			ci.cancel();
	}
}