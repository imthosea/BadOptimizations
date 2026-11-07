package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Deque;
import java.util.List;

@Mixin(ToastComponent.class)
public abstract class MixinToastComponent {
	// List<Entry<?>> but i dont want to make an
	// accesswidener just for Entry when I'm just checking if it's empty'
	@Shadow @Final private List<Object> visible;
	@Shadow @Final private Deque<Toast> queued;

	// Don't do anything if we don't need to
	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;hideGui:Z"))
	private boolean shouldSkipDraw(boolean hudHidden) {
		return hudHidden || (visible.isEmpty() && queued.isEmpty());
	}
}