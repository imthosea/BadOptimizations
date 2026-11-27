package me.thosea.badoptimizations.mixin.debughud.newv;

import me.thosea.badoptimizations.platform.PlatformMethods;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.gui.components.debug.DebugGroups;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenEntries.class)
public class MixinDebug {
	private static final String BO$F3_TEXT = "BadOptimizations " + PlatformMethods.INSTANCE.getVersion();

	@Inject(method = "bootstrap", at = @At("RETURN"))
	private static void onInit(WritableRegistry<DebugScreenEntry> registry, CallbackInfo ci) {
		var key = ResourceKey.create(
				ClientRegistries.DEBUG_SCREEN_ENTRY,
				Identifier.fromNamespaceAndPath("badoptimizations", "version_badoptimizations")
		);
		Registry.register(registry, key, new DebugScreenEntry() {
			@Override
			public void display(
					DebugScreenDisplayer lines,
					Level world,
					LevelChunk clientChunk,
					LevelChunk chunk
			) {
				lines.addToGroup(DebugGroups.MISC, BO$F3_TEXT);
			}

			@Override
			public boolean isAllowed(boolean reducedDebugInfo) {
				return true;
			}
		});
	}
}