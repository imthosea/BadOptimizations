package me.thosea.badoptimizations.mixin.skyangle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.thosea.badoptimizations.interfaces.LevelMethods;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// 1.21.10-
@Mixin(targets = {
		"net.minecraft.world.level.LevelTimeAccess",
		"net.minecraft.class_5424"
}, remap = false)
public interface MixinLevelTimeAccess {
	@WrapOperation(
			method = {"getTimeOfDay", "method_30274"},
			at = {
					@At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;timeOfDay(J)F"),
					@At(value = "INVOKE", target = "Lnet/minecraft/class_2874;method_28528(J)F")
			}
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