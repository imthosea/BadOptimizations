package me.thosea.badoptimizations.mixin.debughud;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.thosea.badoptimizations.platform.PlatformMethods;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = DebugScreenOverlay.class, priority = 999)
public class MixinDebug {
	private static final String BO$F3_TEXT = "BadOptimizations " + PlatformMethods.INSTANCE.getVersion();

	@ModifyReturnValue(method = "getGameInformation", at = @At("RETURN"))
	private List<String> addBadOptimizationsText(List<String> list) {
		list.add("");
		list.add(BO$F3_TEXT);
		return list;
	}
}