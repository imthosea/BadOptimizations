package me.thosea.badoptimizations.mixin.entitydata;

import me.thosea.badoptimizations.interfaces.EntityMethods;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@Mixin(SynchedEntityData.class)
public abstract class MixinSynchedData {
	@Shadow @Final @Mutable private ReadWriteLock lock;
	private EntityMethods bo$entityMethods;

	@Inject(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;setValue(Ljava/lang/Object;)V"))
	private void onDataSet(EntityDataAccessor<?> key, Object value, boolean force, CallbackInfo ci) {
		bo$entityMethods.bo$refreshEntityData(key.getId());
	}

	@Inject(method = "assignValue", at = @At("TAIL"))
	private void onCopy(DataItem<?> to, DataValue<?> from, CallbackInfo ci) {
		bo$entityMethods.bo$refreshEntityData(from.id());
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void replaceLock(Entity trackedEntity, CallbackInfo ci) {
		Lock lock = new Lock() {
			@Override
			public void lock() {}
			@Override
			public void lockInterruptibly() {}
			@Override
			public boolean tryLock() {return true;}
			@Override
			public boolean tryLock(long time, @NotNull TimeUnit unit) {return true;}
			@Override
			public void unlock() {}
			@NotNull
			@Override
			public Condition newCondition() {
				throw new UnsupportedOperationException();
			}
		};

		this.lock = new ReadWriteLock() {
			@NotNull @Override
			public Lock readLock() {return lock;}
			@NotNull @Override
			public Lock writeLock() {return lock;}
		};

		this.bo$entityMethods = (EntityMethods) trackedEntity;
	}
}