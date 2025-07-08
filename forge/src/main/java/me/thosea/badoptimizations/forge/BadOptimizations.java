package me.thosea.badoptimizations.forge;

import me.thosea.badoptimizations.config.BOConfigScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static me.thosea.badoptimizations.config.Config.LOGGER;

@Mod("badoptimizations")
public class BadOptimizations {
	public BadOptimizations(IEventBus bus, ModContainer container) {
		if(FMLEnvironment.dist.isDedicatedServer()) {
			LOGGER.error("BadOptimizations is a client-only mod, this will have no effect on a server.");
			return;
		}

		// wrap in class to avoid loading client classes on server
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