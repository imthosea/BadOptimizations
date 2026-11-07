package me.thosea.badoptimizations.config;

public final class ConfigOptimization {
	public final boolean userValue;
	public final boolean effectiveValue;

	public ConfigOptimization(ConfigLoadContext ctx, String option, boolean loadCondition) {
		this.userValue = !loadCondition || ctx.boolOrDefault(option, true);
		this.effectiveValue = userValue && !ctx.incompats.isIncompatible(option);
	}

	@Override
	public String toString() {
		// technically quite cursed, but stops possible mistakes
		// when typing option.userValue on every value in Config#writeConfig
		return Boolean.toString(userValue);
	}
}