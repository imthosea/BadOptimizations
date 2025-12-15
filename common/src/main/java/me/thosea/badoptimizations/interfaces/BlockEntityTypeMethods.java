package me.thosea.badoptimizations.interfaces;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BlockEntityTypeMethods {
	<T extends BlockEntity> BlockEntityRenderer<T> bo$getRenderer();
	void bo$setRenderer(BlockEntityRenderer<?> renderer);
}