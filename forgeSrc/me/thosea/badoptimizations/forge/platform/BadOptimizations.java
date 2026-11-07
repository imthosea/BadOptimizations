package me.thosea.badoptimizations.forge.platform;

import me.thosea.badoptimizations.screen.BOConfigScreen;
import me.thosea.badoptimizations.platform.PlatformMethods;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory;

@Mod("badoptimizations")
public final class BadOptimizations {
	public BadOptimizations() {
		if(PlatformMethods.INSTANCE.isOnServer()) return;

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