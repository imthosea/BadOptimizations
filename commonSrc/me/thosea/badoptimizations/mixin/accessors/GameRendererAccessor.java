package me.thosea.badoptimizations.mixin.accessors;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
	@Accessor("bossOverlayWorldDarkening")
	float bo$getSkyDarkness();
}