package me.thosea.badoptimizations.mixin.debug;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer.SimpleDebugRenderer;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {
	@Shadow @Final private List<SimpleDebugRenderer> opaqueRenderers;
	@Shadow @Final private List<SimpleDebugRenderer> translucentRenderers;
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(CallbackInfo ci) {
		if(this.opaqueRenderers.isEmpty() && this.translucentRenderers.isEmpty())
			ci.cancel();
	}
}