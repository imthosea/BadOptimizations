package me.thosea.badoptimizations.forge.platform;

import me.thosea.badoptimizations.screen.BOConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "badoptimizations", dist = Dist.CLIENT)
public final class BadOptimizations {
	public BadOptimizations(ModContainer container) {
		container.registerExtensionPoint(IConfigScreenFactory.class, (screen, parent) -> {
			return new BOConfigScreen(parent);
		});
	}
}