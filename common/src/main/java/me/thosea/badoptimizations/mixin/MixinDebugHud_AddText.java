package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.utils.PlatformMethods;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHudEntries.class)
public class MixinDebugHud_AddText {
	private static final String BO$F3_TEXT = "BadOptimizations " + PlatformMethods.getVersion();

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void onInit(CallbackInfo ci) {
		register(
				Identifier.of("badoptimizations", "version_badoptimizations"),
				new DebugHudEntry() {
					@Override
					public void render(
							DebugHudLines lines,
							World world,
							WorldChunk clientChunk,
							WorldChunk chunk
					) {
						lines.addLine(BO$F3_TEXT);
					}

					@Override
					public boolean canShow(boolean reducedDebugInfo) {
						return true;
					}
				}
		);
	}

	@Shadow
	private static Identifier register(Identifier id, DebugHudEntry entry) {
		return null;
	}
}