package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinClientPlayer extends MixinEntity { // renderer.MixinEntity
	@Shadow public abstract PlayerSkin getSkin();

	@Override
	public EntityRenderer<?, ?> bo$getRenderer() {
		return PlayerModelRendererHolder.forModel(getSkin().model());
	}
}