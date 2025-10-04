package me.thosea.badoptimizations.other;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerSkinType;

public final class PlayerModelRendererHolder {
	private PlayerModelRendererHolder() {}

	public static EntityRenderer<? extends PlayerEntity, ?> WIDE_RENDERER;
	public static EntityRenderer<? extends PlayerEntity, ?> SLIM_RENDERER;

	public static EntityRenderer<? extends PlayerEntity, ?> forModel(PlayerSkinType model) {
		if(model == PlayerSkinType.WIDE) {
			return WIDE_RENDERER;
		} else if(model == PlayerSkinType.SLIM) {
			return SLIM_RENDERER;
		} else {
			return null;
		}
	}
}