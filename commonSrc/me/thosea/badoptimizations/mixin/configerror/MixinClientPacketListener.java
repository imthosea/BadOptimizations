package me.thosea.badoptimizations.mixin.configerror;

import me.thosea.badoptimizations.utils.BadVersionHacks;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public final class MixinClientPacketListener {
	private static final ChatComponent bo$chat = BadVersionHacks.getChat();

	@Inject(method = "handleLogin", at = @At("RETURN"))
	private void onLogin(CallbackInfo ci) {
		var msg = Component.literal(
				"Your badoptimizations.txt has errors, please fix or delete it and restart the game."
		);
		bo$chat.addClientSystemMessage(msg);
	}
}