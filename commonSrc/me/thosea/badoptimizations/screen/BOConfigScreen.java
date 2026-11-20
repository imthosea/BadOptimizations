package me.thosea.badoptimizations.screen;

import me.thosea.badoptimizations.config.Config;
import me.thosea.badoptimizations.utils.BadVersionHacks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;

// Opens the config file and exits, generates one if not there
public final class BOConfigScreen extends Screen {
	private final Screen parent;

	public BOConfigScreen(Screen parent) {
		super(Component.empty());
		this.parent = parent;
	}

	@Override
	protected void init() {
		if(!Files.exists(Config.FILE)) {
			try {
				Config.writeConfig();
			} catch(Exception e) {
				throw new RuntimeException("Failed to generate BadOptimizations config", e);
			}
		}
		BadVersionHacks.openUri(Config.FILE.toUri());
		minecraft.setScreenAndShow(parent);
	}
}