package me.thosea.badoptimizations.screen;

import com.mojang.blaze3d.Blaze3D;
import me.thosea.badoptimizations.config.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;

// Opens the config file and exits, generates one if not there
public final class BOConfigScreen extends Screen {
	// 26.1/2-Util.getPlatform().openUri
	// 26.3-Blaze3D.openUri
	// we use normal reflection, rather than MethodHandle,
	// mostly because this class won't get JIT inlined anyway
	private static final Object openUriObj;
	private static final Method openUri;

	static {
		Method method;
		Object obj;
		try {
			method = Blaze3D.class.getMethod("openUri", URI.class);
			obj = null;
		} catch(NoSuchMethodException e) {
			try {
				// noinspection JavaReflectionMemberAccess
				method = OS.class.getMethod("openUri", URI.class);
				obj = Util.getPlatform();
			} catch(NoSuchMethodException ex) {
				throw new RuntimeException(ex);
			}
		}

		openUriObj = obj;
		openUri = method;
	}

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
		try {
			openUri.invoke(openUriObj, Config.FILE.toUri());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		minecraft.setScreenAndShow(parent);
	}
}