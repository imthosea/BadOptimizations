package me.thosea.badoptimizations.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ParticleEngine.class)
public class MixinParticleManager {
	@Shadow @Final private Map<?, ?> particles; // <ParticleRenderType, Queue<Particle>>

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(PoseStack matrices, BufferSource vertexConsumers, LightTexture lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci) {
		if(particles.isEmpty()) {
			ci.cancel();
		}
	}
}