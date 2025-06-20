package me.thosea.badoptimizations.forge;

import me.thosea.badoptimizations.other.BOConfigScreen;
import me.thosea.badoptimizations.other.Config;
import me.thosea.badoptimizations.utils.PlatformMethods;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory;

@Mod("badoptimizations")
public class BadOptimizations {
	public BadOptimizations() {
		if(PlatformMethods.isOnServer()) {
			Config.LOGGER.error("BadOptimizations is a client-only mod, this will have no effect on a server.");
			return;
		}

		// avoid loading client classes on server
		ClientInit.init();
	}

	private static class ClientInit {
		private static void init() {
			ModLoadingContext.get().registerExtensionPoint(
					ConfigScreenHandler.ConfigScreenFactory.class,
					() -> new ConfigScreenFactory((mc, parent) -> new BOConfigScreen(parent))
			);
		}
	}
}