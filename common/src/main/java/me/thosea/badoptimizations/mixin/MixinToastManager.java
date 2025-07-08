package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.toast.NowPlayingToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.ToastManager.Entry;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Deque;
import java.util.List;
import java.util.Set;

@Mixin(ToastManager.class)
public abstract class MixinToastManager {
	@Shadow @Final private List<Object> visibleEntries;
	@Shadow @Final private Deque<Toast> toastQueue;
	@Shadow @Final private Set<SoundEvent> queuedToastSounds;
	@Shadow private Entry<NowPlayingToast> nowPlayingToast;

	// Don't do anything if we don't need to
	@ModifyExpressionValue(method = "draw", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;hudHidden:Z"))
	private boolean shouldSkipDraw(boolean hudHidden) {
		if(hudHidden) return true;

		return visibleEntries.isEmpty()
				&& toastQueue.isEmpty()
				&& queuedToastSounds.isEmpty()
				&& nowPlayingToast == null;
	}
}