package me.thosea.badoptimizations.mixin.debug;

import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer.Renderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {
	@Shadow @Final private List<Renderer> debugRenderers;
	@Shadow @Final private List<Renderer> lateDebugRenderers;
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(CallbackInfo ci) {
		if(this.debugRenderers.isEmpty() && this.lateDebugRenderers.isEmpty())
			ci.cancel();
	}
}