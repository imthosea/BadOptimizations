package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import net.minecraft.client.network.ClientMannequinEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientMannequinEntity.class)
public abstract class MixinMannequinPlayer extends MixinEntity {  // renderer.MixinEntity
	@Shadow public abstract SkinTextures getSkin();

	@Override
	public EntityRenderer<?, ?> bo$getRenderer() {
		return PlayerModelRendererHolder.forModel(getSkin().model());
	}
}