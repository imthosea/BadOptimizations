package me.thosea.badoptimizations.mixin.entitydata;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = LivingEntity.class, priority = 700)
public abstract class MixinLivingEntity extends MixinEntity {
	@Shadow @Final protected static EntityDataAccessor<Byte> DATA_LIVING_ENTITY_FLAGS;
	@Shadow @Final private static EntityDataAccessor<Float> DATA_HEALTH_ID;
	@Shadow @Final private static EntityDataAccessor<Integer> DATA_EFFECT_COLOR_ID;
	@Shadow @Final private static EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID;
	@Shadow @Final private static EntityDataAccessor<Integer> DATA_ARROW_COUNT_ID;
	@Shadow @Final private static EntityDataAccessor<Integer> DATA_STINGER_COUNT_ID;
	@Shadow @Final private static EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID;

	private boolean bo$isUsingItem = false;
	private boolean bo$potionSwirlsAmbient = false;
	private boolean bo$isUsingRiptide = false;
	private InteractionHand bo$activeHand = InteractionHand.MAIN_HAND;
	private float bo$health = 1.0f;
	private int bo$potionSwirlsColor = 0;
	private int bo$stuckArrowCount = 0;
	private int bo$stingerCount = 0;
	private Optional<BlockPos> bo$sleepingPosition = Optional.empty();

	@Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;get(Lnet/minecraft/network/syncher/EntityDataAccessor;)Ljava/lang/Object;", ordinal = 0))
	private Object onGetPotionSwirlsColor(SynchedEntityData instance, EntityDataAccessor<Integer> data) {
		return bo$potionSwirlsColor;
	}

	@Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;get(Lnet/minecraft/network/syncher/EntityDataAccessor;)Ljava/lang/Object;", ordinal = 1))
	private Object onGetPotionSwirlsAmbient(SynchedEntityData instance, EntityDataAccessor<Boolean> data) {
		return bo$potionSwirlsAmbient;
	}

	@Overwrite public boolean isUsingItem() {return bo$isUsingItem;}
	@Overwrite public InteractionHand getUsedItemHand() {return bo$activeHand;}
	@Overwrite public boolean isAutoSpinAttack() {return bo$isUsingRiptide;}
	@Overwrite public float getHealth() {return bo$health;}
	@Overwrite public final int getArrowCount() {return bo$stuckArrowCount;}
	@Overwrite public final int getStingerCount() {return bo$stingerCount;}
	@Overwrite public Optional<BlockPos> getSleepingPos() {return bo$sleepingPosition;}

	@Override
	public void bo$refreshEntityData(int data) {
		super.bo$refreshEntityData(data);

		if(data == DATA_LIVING_ENTITY_FLAGS.getId()) {
			bo$isUsingItem = (entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
			bo$activeHand = (entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
			bo$isUsingRiptide = (entityData.get(DATA_LIVING_ENTITY_FLAGS) & 4) != 0;
		} else if(data == DATA_HEALTH_ID.getId()) {
			bo$health = entityData.get(DATA_HEALTH_ID);
		} else if(data == DATA_EFFECT_COLOR_ID.getId()) {
			bo$potionSwirlsColor = entityData.get(DATA_EFFECT_COLOR_ID);
		} else if(data == DATA_EFFECT_AMBIENCE_ID.getId()) {
			bo$potionSwirlsAmbient = entityData.get(DATA_EFFECT_AMBIENCE_ID);
		} else if(data == DATA_ARROW_COUNT_ID.getId()) {
			bo$stuckArrowCount = entityData.get(DATA_ARROW_COUNT_ID);
		} else if(data == DATA_STINGER_COUNT_ID.getId()) {
			bo$stingerCount = entityData.get(DATA_STINGER_COUNT_ID);
		} else if(data == SLEEPING_POS_ID.getId()) {
			bo$sleepingPosition = entityData.get(SLEEPING_POS_ID);
		}
	}
}