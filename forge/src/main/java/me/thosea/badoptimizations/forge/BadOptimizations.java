package me.thosea.badoptimizations.forge;

import me.thosea.badoptimizations.config.BOConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mod("badoptimizations")
public class BadOptimizations {
	public BadOptimizations() {
		if(FMLEnvironment.dist.isDedicatedServer()) {
			LOGGER.error("BadOptimizations is a client-only mod, this will have no effect on a server.");
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