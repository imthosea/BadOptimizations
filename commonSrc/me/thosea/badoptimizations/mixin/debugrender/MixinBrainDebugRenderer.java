package me.thosea.badoptimizations.mixin.debugrender;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BrainDebugRenderer.class)
public class MixinBrainDebugRenderer {
	@Shadow @Final private Map<?, ?> pois; // <BlockPos, PoiInfo>
	@Shadow @Final private Map<?, ?> brainDumpsPerEntity; // <UUID, BrainDump>

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if(pois.isEmpty() && brainDumpsPerEntity.isEmpty())
			ci.cancel();
	}
}