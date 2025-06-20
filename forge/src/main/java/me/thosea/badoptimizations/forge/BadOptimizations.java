package me.thosea.badoptimizations.forge;

import me.thosea.badoptimizations.other.BOConfigScreen;
import me.thosea.badoptimizations.other.Config;
import me.thosea.badoptimizations.utils.PlatformMethods;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("badoptimizations")
public class BadOptimizations {
	public BadOptimizations(IEventBus bus, ModContainer container) {
		if(PlatformMethods.isOnServer()) {
			Config.LOGGER.error("BadOptimizations is a client-only mod, this will have no effect on a server.");
			return;
		}

		// avoid loading client classes on server
		ClientInit.init(container);
	}

	private static class ClientInit {
		private static void init(ModContainer container) {
			container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> {
				return new BOConfigScreen(parent);
			});
		}
	}
}