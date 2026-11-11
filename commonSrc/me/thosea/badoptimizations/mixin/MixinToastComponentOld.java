package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Deque;
import java.util.List;

@Mixin(ToastManager.class)
public abstract class MixinToastComponentOld {
	@Shadow @Final private List<?> visibleToasts;
	@Shadow @Final private Deque<?> queued;

	// Don't do anything if we don't need to
	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;hideGui:Z"))
	private boolean shouldSkipDraw(boolean hudHidden) {
		if(hudHidden) return true;

		return visibleToasts.isEmpty() && queued.isEmpty();
	}
}