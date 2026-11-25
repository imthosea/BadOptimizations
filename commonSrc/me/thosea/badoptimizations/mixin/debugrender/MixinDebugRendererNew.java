package me.thosea.badoptimizations.mixin.debugrender;

import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugRenderer.class)
public class MixinDebugRendererNew {
	@Shadow @Final private List<?> renderers; // <SimpleDebugRenderer>
	@Inject(method = "emitGizmos", at = @At("HEAD"), cancellable = true)
	private void onRender(CallbackInfo ci) {
		if(this.renderers.isEmpty())
			ci.cancel();
	}
}