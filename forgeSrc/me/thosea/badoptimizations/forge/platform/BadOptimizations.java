package me.thosea.badoptimizations.forge.platform;

import me.thosea.badoptimizations.platform.PlatformMethods;
import me.thosea.badoptimizations.screen.BOConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("badoptimizations")
public class BadOptimizations {
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