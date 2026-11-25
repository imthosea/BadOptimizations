package me.thosea.badoptimizations.other;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;

// i must capture the avatar to restore my honor
public final class AvatarModelRendererHolder {
	private AvatarModelRendererHolder() {}

	public static EntityRenderer<? extends Player, ?> PLAYER_WIDE_RENDERER;
	public static EntityRenderer<? extends Player, ?> PLAYER_SLIM_RENDERER;

	public static EntityRenderer<? extends Mannequin, ?> MANNEQUIN_WIDE_RENDERER;
	public static EntityRenderer<? extends Mannequin, ?> MANNEQUIN_SLIM_RENDERER;

	public static EntityRenderer<? extends Player, ?> forPlayerModel(PlayerModelType model) {
		if(model == PlayerModelType.WIDE) {
			return PLAYER_WIDE_RENDERER;
		} else if(model == PlayerModelType.SLIM) {
			return PLAYER_SLIM_RENDERER;
		} else {
			return null;
		}
	}

	public static EntityRenderer<? extends Mannequin, ?> forMannequinModel(PlayerModelType model) {
		if(model == PlayerModelType.WIDE) {
			return MANNEQUIN_WIDE_RENDERER;
		} else if(model == PlayerModelType.SLIM) {
			return MANNEQUIN_SLIM_RENDERER;
		} else {
			return null;
		}
	}
}