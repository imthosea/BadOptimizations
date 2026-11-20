package me.thosea.badoptimizations.utils;

import com.mojang.blaze3d.Blaze3D;
import me.thosea.badoptimizations.reflect.BadReflection;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.joml.Vector3fc;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.net.URI;

// 26.1-3
public final class BadVersionHacks {
	private BadVersionHacks() {}

	private static final MethodHandle getCamera = BadReflection.getVirtual(
			GameRenderer.class,
			MethodType.methodType(Camera.class),
			"mainCamera", // 26.1
			"getMainCamera" // 26.2+
	);
	public static Camera getCamera(GameRenderer renderer) {
		try {
			return (Camera) getCamera.invokeExact(renderer);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	// 26.1/2 -> EnvironmentAttribute<Integer>, 26.3 -> EnvironmentAttribute<Vector3fc>
	public static final boolean IS_SKY_COLOR_VEC =
			((EnvironmentAttribute<?>) EnvironmentAttributes.SKY_LIGHT_COLOR)
					.defaultValue() instanceof Vector3fc;

	// 26.1/2:
	// Util.getPlatform().openUri
	// 26.3:
	// Blaze3D.openUri
	// use normal reflection here cause this isn't gonna get JIT'd anyway
	private static final Object objOpenUri;
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
		objOpenUri = obj;
		openUri = method;
	}
	public static void openUri(URI uri) {
		try {
			openUri.invoke(objOpenUri, uri);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("UNCHECKED_CAST")
	public static <T> T cast(Object object) {
		return (T) object;
	}
}