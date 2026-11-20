package me.thosea.badoptimizations.mixin.debug;

import net.minecraft.client.renderer.debug.GameTestDebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GameTestDebugRenderer.class)
public class MixinGameTestDebugRenderer {
	@Shadow @Final private Map<?, ?> markers; // <BlockPos, Marker>

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(CallbackInfo ci) {
		if(markers.isEmpty()) ci.cancel();
	}
}