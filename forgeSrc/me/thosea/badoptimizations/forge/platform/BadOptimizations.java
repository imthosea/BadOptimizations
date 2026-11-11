package me.thosea.badoptimizations.forge.platform;

import me.thosea.badoptimizations.screen.BOConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(value = "badoptimizations", dist = Dist.CLIENT)
public final class BadOptimizations {
	@SuppressWarnings("unused")
	public BadOptimizations(ModContainer mod) {
		mod.registerExtensionPoint(IConfigScreenFactory.class, new IConfigScreenFactory() {
			@Override
			@NotNull
			public Screen createScreen(@NotNull ModContainer mod, @NotNull Screen parent) {
				return new BOConfigScreen(parent);
			}

			// override for 1.20.5/1.20.6
			@NotNull
			public Screen createScreen(Minecraft mc, Screen parent) {
				return new BOConfigScreen(parent);
			}
		});
	}
}