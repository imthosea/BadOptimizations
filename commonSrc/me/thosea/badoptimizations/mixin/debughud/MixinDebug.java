package me.thosea.badoptimizations.mixin.debughud;

import me.thosea.badoptimizations.platform.PlatformMethods;
import me.thosea.badoptimizations.utils.BadVersionHacks;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.invoke.MethodHandles;

@Mixin(DebugScreenEntries.class)
public class MixinDebug {
	private static final String BO$F3_TEXT = "BadOptimizations " + PlatformMethods.INSTANCE.getVersion();

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void onInit(CallbackInfo ci) {
		BadVersionHacks.registerDebug(
				MethodHandles.lookup(),
				BadVersionHacks.makeIdentifier("badoptimizations", "version_badoptimizations"),
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
}