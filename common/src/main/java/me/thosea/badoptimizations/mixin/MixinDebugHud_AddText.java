package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.utils.PlatformMethods;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(value = DebugHud.class, priority = 999)
public class MixinDebugHud_AddText {
	private static final String BO$F3_TEXT = "BadOptimizations " + PlatformMethods.getVersion();

	@ModifyArg(method = "render", index = 1, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V"))
	private List<String> addBadOptimizationsText(List<String> list) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.world != null && client.debugHudEntryList.isF3Enabled()) {
			list.add("");
			list.add(BO$F3_TEXT);
		}
		return list;
	}
}