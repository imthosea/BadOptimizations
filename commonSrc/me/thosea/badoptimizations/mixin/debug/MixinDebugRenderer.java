package me.thosea.badoptimizations.mixin.debug;

import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer.SimpleDebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {
	@Shadow @Final private List<SimpleDebugRenderer> renderers;

	@Inject(method = "emitGizmos", at = @At("HEAD"), cancellable = true)
	private void onRender(CallbackInfo ci) {
		if(this.renderers.isEmpty())
			ci.cancel();
	}
}