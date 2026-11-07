package me.thosea.badoptimizations.interfaces;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

public interface EntityMethods {
	void bo$refreshEntityData(int data);

	<T extends Entity> EntityRenderer<T, ?> bo$getRenderer();
}