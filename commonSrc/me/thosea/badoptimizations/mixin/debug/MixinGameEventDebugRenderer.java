package me.thosea.badoptimizations.mixin.debug;

import net.minecraft.client.renderer.debug.GameEventListenerRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GameEventListenerRenderer.class)
public class MixinGameEventDebugRenderer {
	@Shadow @Final private List<?> trackedGameEvents; // <TrackedGameEvent>

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(CallbackInfo ci) {
		if(trackedGameEvents.isEmpty()) ci.cancel();
	}
}