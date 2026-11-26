package me.thosea.badoptimizations.fabric.platform;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.thosea.badoptimizations.screen.BOConfigScreen;

@SuppressWarnings("unused")
public final class BadOptimizationsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return BOConfigScreen::new;
	}
}