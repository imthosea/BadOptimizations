package me.thosea.badoptimizations.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
public class MixinParticleManager {
	@Shadow @Final private Map<ParticleRenderType, Queue<Particle>> particles;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(PoseStack matrices, BufferSource vertexConsumers, LightTexture lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci) {
		if(particles.isEmpty()) {
			ci.cancel();
		}
	}
}