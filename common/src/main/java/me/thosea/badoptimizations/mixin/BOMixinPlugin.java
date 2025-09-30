package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.config.Config;
import me.thosea.badoptimizations.utils.PlatformMethods;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BOMixinPlugin implements IMixinConfigPlugin {
	private static final boolean isOnServer = PlatformMethods.isOnServer();

	@Override
	public void onLoad(String mixinPackage) {
		if(isOnServer) return;
		Config.init();
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixin) {
		if(isOnServer) return false;

		mixin = mixin.substring("me.thosea.badoptimizations.mixin.".length());

		if(mixin.equals("tick.MixinLightmapManager") || mixin.equals("accessors.GameRendererAccessor") || mixin.equals("accessors.PlayerAccessor")) {
			return Config.lightmapCaching.effectiveValue;
		} else if(mixin.equals("tick.MixinClientWorld")) {
			return Config.skyColorCaching.effectiveValue;
		} else if(mixin.startsWith("debug.")) {
			return Config.debugRendererDisableIfNotNeeded.effectiveValue;
		} else if(mixin.equals("MixinParticleManager")) {
			return Config.particleManagerOptimization.effectiveValue;
		} else if(mixin.equals("MixinToastManager")) {
			return Config.toastOptimizations.effectiveValue;
		} else if(mixin.equals("MixinWorldRenderer")) {
			return Config.skyAngleCaching.effectiveValue;
		} else if(mixin.startsWith("renderer.entity.")) {
			return Config.entityRendererCaching.effectiveValue;
		} else if(mixin.startsWith("renderer.blockentity.")) {
			return Config.blockEntityRendererCaching.effectiveValue;
		} else if(mixin.startsWith("entitydata.")) {
			return Config.entityFlagCaching.effectiveValue;
		} else if(mixin.equals("tick.MixinGameRenderer")) {
			return Config.removeRedundantFovCalcs.effectiveValue;
		} else if(mixin.equals("tick.MixinTutorial")) {
			return Config.removeTutorialIfNotDemo.effectiveValue;
		} else if(mixin.equals("MixinDebugHud_AddText")) {
			return Config.showF3Text;
		}

		throw new RuntimeException("No config option for mixin " + mixin);
	}

	// blah
	@Override public String getRefMapperConfig() {return null;}
	@Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
	@Override public List<String> getMixins() {return null;}
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}