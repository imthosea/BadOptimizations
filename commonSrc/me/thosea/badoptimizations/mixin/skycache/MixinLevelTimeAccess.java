package me.thosea.badoptimizations.mixin.skycache;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.thosea.badoptimizations.interfaces.LevelMethods;
import net.minecraft.world.level.LevelTimeAccess;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelTimeAccess.class)
public interface MixinLevelTimeAccess {
	@WrapOperation(
			method = {"getTimeOfDay"},
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;timeOfDay(J)F")
	)
	private float onGetTimeOfDay(DimensionType type, long time, Operation<Float> original) {
		// getTimeOfDay method takes a partial tick param but it does nothing so we ignore it
		if(this instanceof LevelMethods methods) {
			if(time == methods.bo$getTimeOfDayLastTime()) {
				return methods.bo$getTimeOfDayCache();
			} else {
				float result = original.call(type, time);
				methods.bo$setTimeOfDayCache(time, result);
				return result;
			}
		}
		return original.call(type, time);
	}
}