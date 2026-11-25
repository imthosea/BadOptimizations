package me.thosea.badoptimizations.mixin.toast.oldv;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Deque;
import java.util.List;

// 1.21.5-
@Mixin(ToastManager.class)
public abstract class MixinToastManager {
	@Shadow @Final private List<?> visibleToasts; // <ToastInstance<?>>
	@Shadow @Final private Deque<?> queued; // <Toast>

	// Don't do anything if we don't need to
	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;hideGui:Z"))
	private boolean shouldSkipDraw(boolean hudHidden) {
		if(hudHidden) return true;

		return visibleToasts.isEmpty() && queued.isEmpty();
	}
}