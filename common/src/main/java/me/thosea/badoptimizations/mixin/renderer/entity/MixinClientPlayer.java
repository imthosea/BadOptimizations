package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinClientPlayer extends MixinEntity { // renderer.MixinEntity
	@Shadow public abstract SkinTextures getSkin();

	@Override
	public EntityRenderer<?, ?> bo$getRenderer() {
		PlayerSkinType model = getSkin().model();

		if(model == PlayerSkinType.WIDE) {
			return PlayerModelRendererHolder.WIDE_RENDERER;
		} else if(model == PlayerSkinType.SLIM) {
			return PlayerModelRendererHolder.SLIM_RENDERER;
		} else {
			return null;
		}
	}
}