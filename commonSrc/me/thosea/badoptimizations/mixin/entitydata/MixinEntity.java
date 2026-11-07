package me.thosea.badoptimizations.mixin.entitydata;

import me.thosea.badoptimizations.interfaces.EntityMethods;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = Entity.class, priority = 700)
public abstract class MixinEntity implements EntityMethods {
	@Shadow @Final private static EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME;
	@Shadow @Final protected static EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID;
	@Shadow @Final private static EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE;
	@Shadow @Final private static EntityDataAccessor<Boolean> DATA_SILENT;
	@Shadow @Final private static EntityDataAccessor<Boolean> DATA_NO_GRAVITY;
	@Shadow @Final protected static EntityDataAccessor<Pose> DATA_POSE;
	@Shadow @Final private static EntityDataAccessor<Integer> DATA_TICKS_FROZEN;
	@Shadow @Final private static EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID;

	@Shadow private Level level;

	private boolean bo$glowingClient;
	private boolean bo$onFire = false;
	private boolean bo$sneaking = false;
	private boolean bo$sprinting = false;
	private boolean bo$swimming = false;
	private boolean bo$invisible = false;
	private boolean bo$nameVisible = false;
	private boolean bo$silent = false;
	private boolean bo$noGravity = false;
	private int bo$frozenTicks = 0;
	private Pose bo$pose = Pose.STANDING;
	private Optional<Component> bo$customName = Optional.empty();
	private int bo$remainingAirTicks = getMaxAirSupply();

	@Shadow public abstract int getMaxAirSupply();

	@Redirect(method = "isOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getSharedFlag(I)Z"))
	private boolean getIsOnFire(Entity instance, int index) {
		return bo$onFire;
	}

	@Shadow private boolean hasGlowingTag; // vanilla glowing on the server

	@Overwrite public boolean isCurrentlyGlowing() {return level.isClientSide ? bo$glowingClient : hasGlowingTag;}
	@Overwrite public boolean isShiftKeyDown() {return bo$sneaking;}
	@Overwrite public boolean isSprinting() {return bo$sprinting;}
	@Overwrite public boolean isSwimming() {return bo$swimming;}
	@Overwrite public boolean isInvisible() {return bo$invisible;}
	@Overwrite public int getAirSupply() {return bo$remainingAirTicks;}
	@Overwrite @Nullable public Component getCustomName() {return bo$customName.orElse(null);}
	@Overwrite public boolean hasCustomName() {return bo$customName.isPresent();}
	@Overwrite public boolean isCustomNameVisible() {return bo$nameVisible;}
	@Overwrite public boolean isSilent() {return bo$silent;}
	@Overwrite public boolean isNoGravity() {return bo$noGravity;}
	@Overwrite public Pose getPose() {return bo$pose;}
	@Overwrite public int getTicksFrozen() {return bo$frozenTicks;}

	@Shadow @Final protected SynchedEntityData entityData;

	@Override
	public void bo$refreshEntityData(int data) {
		if(data == DATA_SHARED_FLAGS_ID.getId()) {
			byte flags = entityData.get(DATA_SHARED_FLAGS_ID);

			bo$onFire = bo$getFlag(flags, 0);
			bo$sneaking = bo$getFlag(flags, 1);
			bo$sprinting = bo$getFlag(flags, 3);
			bo$swimming = bo$getFlag(flags, 4);
			bo$invisible = bo$getFlag(flags, 5);
			if(level.isClientSide) {
				bo$glowingClient = bo$getFlag(flags, 6);
			}
		} else if(data == DATA_AIR_SUPPLY_ID.getId()) {
			bo$remainingAirTicks = entityData.get(DATA_AIR_SUPPLY_ID);
		} else if(data == DATA_CUSTOM_NAME.getId()) {
			bo$customName = entityData.get(DATA_CUSTOM_NAME);
		} else if(data == DATA_CUSTOM_NAME_VISIBLE.getId()) {
			bo$nameVisible = entityData.get(DATA_CUSTOM_NAME_VISIBLE);
		} else if(data == DATA_SILENT.getId()) {
			bo$silent = entityData.get(DATA_SILENT);
		} else if(data == DATA_NO_GRAVITY.getId()) {
			bo$noGravity = entityData.get(DATA_NO_GRAVITY);
		} else if(data == DATA_POSE.getId()) {
			bo$pose = entityData.get(DATA_POSE);
		} else if(data == DATA_TICKS_FROZEN.getId()) {
			bo$frozenTicks = entityData.get(DATA_TICKS_FROZEN);
		}
	}

	private boolean bo$getFlag(byte flags, int index) {
		return (flags & 1 << index) != 0;
	}
}