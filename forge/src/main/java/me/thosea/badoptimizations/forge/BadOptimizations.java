package me.thosea.badoptimizations.forge;

import me.thosea.badoptimizations.config.BOConfigScreen;
import me.thosea.badoptimizations.utils.PlatformMethods;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("badoptimizations")
public class BadOptimizations {
	public BadOptimizations() {
		if(PlatformMethods.isOnServer()) return;

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