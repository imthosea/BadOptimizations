package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.config.Config;
import me.thosea.badoptimizations.hook.CacheHooks;
import me.thosea.badoptimizations.platform.PlatformMethods;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public final class BOMixinPlugin implements IMixinConfigPlugin {
	private static final boolean isOnServer = PlatformMethods.INSTANCE.isOnServer();
	// remap-independent way to get it
	private static final int mixinPackageLength = BOMixinPlugin.class.getName().indexOf("BOMixinPlugin");

	@Override
	public void onLoad(String mixinPackage) {
		if(isOnServer) return;
		Config.init();
		CacheHooks.init();
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixin) {
		if(isOnServer) return false;
		String[] split = mixin.substring(mixinPackageLength).split("\\.");
		return switch(split[0]) {
			case "configerror" -> Config.isError;
			case "debughud" -> Config.showF3Text;
			case "debugrender" -> Config.debugRendererDisableIfNotNeeded;
			case "entitydata" -> Config.entityFlagCaching;
			case "fov" -> Config.removeRedundantFovCalcs;
			case "lightmap" -> Config.lightmapCaching;
			case "particle" -> Config.particleManagerOptimization;
			case "renderblockentity" -> Config.blockEntityRendererCaching;
			case "renderentity" -> Config.entityRendererCaching;
			case "skyangle" -> Config.skyAngleCaching;
			case "skycolor" -> Config.skyColorCaching;
			case "toast" -> Config.toastOptimizations;
			case "tutorial" -> Config.removeTutorialIfNotDemo;
			default -> throw new RuntimeException("No config option for mixin " + mixin);
		};
	}

	// blah
	@Override
	public String getRefMapperConfig() {return null;}
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
	@Override
	public List<String> getMixins() {return null;}
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}