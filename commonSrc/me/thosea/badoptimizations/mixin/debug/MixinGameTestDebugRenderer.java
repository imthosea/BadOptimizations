package me.thosea.badoptimizations.mixin.debug;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.GameTestDebugRenderer;
import net.minecraft.core.BlockPos;

@Mixin(GameTestDebugRenderer.class)
public class MixinGameTestDebugRenderer {
	@Shadow @Final private Map<BlockPos, ?> markers; // i. do not want to use accesswidener

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if(markers.isEmpty()) ci.cancel();
	}
}