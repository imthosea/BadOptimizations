package me.thosea.badoptimizations.mixin.configerror;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public final class MixinClientPacketListener {
	@Inject(method = "handleLogin", at = @At("RETURN"))
	private void onLogin(CallbackInfo ci) {
		var msg = Component.literal(
				"Your badoptimizations.txt has errors, please fix or delete it and restart the game."
		);
		Minecraft.getInstance().gui.getChat().addMessage(msg);
	}
}