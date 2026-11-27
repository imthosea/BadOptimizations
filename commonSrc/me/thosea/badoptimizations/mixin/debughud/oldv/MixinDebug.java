package me.thosea.badoptimizations.mixin.debughud.oldv;

import me.thosea.badoptimizations.platform.PlatformMethods;
import me.thosea.badoptimizations.utils.BadVersionHacks;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenEntries.class)
public class MixinDebug {
	private static final String BO$F3_TEXT = "BadOptimizations " + PlatformMethods.INSTANCE.getVersion();

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
						BadVersionHacks.addLine(lines, BO$F3_TEXT);
					}

					@Override
					public boolean isAllowed(boolean reducedDebugInfo) {
						return true;
					}
				}
		);
	}

	@SuppressWarnings("MixinAnnotationTarget")
	@Contract // pure = false
	@Shadow
	private static Identifier register(Identifier id, DebugScreenEntry entry) {
		return null;
	}
}