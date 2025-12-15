package me.thosea.badoptimizations.mixin.accessors;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocalPlayer.class)
public interface PlayerAccessor {
	@Accessor("waterVisionTime")
	int bo$underwaterVisibilityTicks();
}