package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.interfaces.EntityMethods;
import me.thosea.badoptimizations.interfaces.EntityTypeMethods;
import me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Map.Entry;

@Mixin(value = EntityRenderDispatcher.class, priority = 700)
public abstract class MixinEntityRendererDispatcher {
	@Shadow private Map<EntityType<?>, EntityRenderer<?, ?>> renderers;
	@Shadow private Map<PlayerModelType, EntityRenderer<? extends Player, ?>> mannequinRenderers;

	@Overwrite
	public <T extends Entity & EntityMethods> EntityRenderer<? super T, ?> getRenderer(T entity) {
		var renderer = entity.bo$getRenderer();
		if(renderer != null) {
			return renderer;
		} else {
			return bo$getOtherRenderer(entity);
		}
	}

	private <T extends Entity & EntityMethods> EntityRenderer<? super T, ?> bo$getOtherRenderer(T entity) {
		// some mods inject renderers late, or add custom unsupported player models
		if(entity instanceof ClientAvatarEntity player) {
			var renderer = mannequinRenderers.get(player.getSkin().model());
			if(renderer != null) {
				return (EntityRenderer<? super T, ?>) renderer;
			} else {
				return (EntityRenderer<? super T, ?>) this.mannequinRenderers.get(PlayerModelType.WIDE);
			}
		} else {
			return (EntityRenderer<? super T, ?>) this.renderers.get(entity.getType());
		}
	}

	@Inject(method = "onResourceManagerReload", at = @At("RETURN"))
	private void afterReload(ResourceManager manager, CallbackInfo ci) {
		for(Entry<EntityType<?>, EntityRenderer<?, ?>> entry : renderers.entrySet()) {
			((EntityTypeMethods) entry.getKey()).bo$setRenderer(entry.getValue());
		}

		// Used by MixinClientPlayer
		PlayerModelRendererHolder.WIDE_RENDERER = mannequinRenderers.get(PlayerModelType.WIDE);
		PlayerModelRendererHolder.SLIM_RENDERER = mannequinRenderers.get(PlayerModelType.SLIM);
	}
}