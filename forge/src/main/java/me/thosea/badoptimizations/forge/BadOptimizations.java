package me.thosea.badoptimizations.forge;

import me.thosea.badoptimizations.other.BOConfigScreen;
import me.thosea.badoptimizations.other.Config;
import me.thosea.badoptimizations.utils.PlatformMethods;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

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