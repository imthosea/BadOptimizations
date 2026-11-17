package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.utils.BadVersionHacks;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public final class MixinAbstractClientPlayer {
	private static final Options bo$options = Minecraft.getInstance().options;

	// don't do unneeded FOV calculations
	@Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
	private void onGetFovModifier(CallbackInfoReturnable<Float> cir) {
		if(bo$options.fovEffectScale().get() == 0) {
			Player player = BadVersionHacks.cast(this);
			cir.setReturnValue(
					bo$options.getCameraType() == CameraType.FIRST_PERSON && player.isScoping()
							? 0.1f
							: 1.0f
			);
		}
	}
}