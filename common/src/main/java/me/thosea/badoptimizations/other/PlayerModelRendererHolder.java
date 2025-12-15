package me.thosea.badoptimizations.other;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.player.Player;

public final class PlayerModelRendererHolder {
	private PlayerModelRendererHolder() {}

	public static EntityRenderer<? extends Player, ?> WIDE_RENDERER;
	public static EntityRenderer<? extends Player, ?> SLIM_RENDERER;
}