package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.utils.PlatformMethods;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenEntries.class)
public class MixinDebugHud_AddText {
	private static final String BO$F3_TEXT = "BadOptimizations " + PlatformMethods.getVersion();

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void onInit(CallbackInfo ci) {
		register(
				Identifier.fromNamespaceAndPath("badoptimizations", "version_badoptimizations"),
				new DebugScreenEntry() {
					@Override
					public void display(
							DebugScreenDisplayer lines,
							Level world,
							LevelChunk clientChunk,
							LevelChunk chunk
					) {
						lines.addLine(BO$F3_TEXT);
					}

					@Override
					public boolean isAllowed(boolean reducedDebugInfo) {
						return true;
					}
				}
		);
	}

	@Shadow
	private static Identifier register(Identifier id, DebugScreenEntry entry) {
		return null;
	}
}