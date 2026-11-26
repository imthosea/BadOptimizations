package me.thosea.badoptimizations.mixin.debugrender.oldv;

import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {
	@Shadow(aliases = "field_63070", remap = false) @Final
	private List<?> opaqueRenderers; // <SimpleDebugRenderer>
	@Shadow(aliases = "field_63071", remap = false) @Final
	private List<?> translucentRenderers; // <SimpleDebugRenderer>

	@SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
	@Inject(remap = false, method = {"render", "method_23099"}, at = @At("HEAD"), cancellable = true)
	private void onRender(CallbackInfo ci) {
		if(this.opaqueRenderers.isEmpty() && this.translucentRenderers.isEmpty())
			ci.cancel();
	}
}