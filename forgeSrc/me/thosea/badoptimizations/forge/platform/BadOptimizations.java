package me.thosea.badoptimizations.forge.platform;

import me.thosea.badoptimizations.screen.BOConfigScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("badoptimizations")
public final class BadOptimizations {
	public BadOptimizations(IEventBus bus, ModContainer container) {
		container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> {
			return new BOConfigScreen(parent);
		});
	}
}