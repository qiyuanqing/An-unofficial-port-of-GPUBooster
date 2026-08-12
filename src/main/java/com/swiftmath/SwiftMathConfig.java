package com.swiftmath;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SwiftMathConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue FAST_MATH = BUILDER
            .comment("Use a faster JOML matrix implementation for pose/normal matrices.")
            .define("fastMath", true);

    public static final ModConfigSpec.BooleanValue FAST_RANDOM = BUILDER
            .comment("Use a table-based Gaussian generator instead of the vanilla Marsaglia polar method.")
            .define("fastRandom", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    // Vanilla constructs some RandomSource/PoseStack.Pose instances during very early static
    // bootstrap (e.g. biome registry init), before FML has finished loading this mod's config.
    // Fall back to the default (enabled) rather than let ModConfigSpec throw in that window.
    public static boolean fastMathEnabled() {
        return !SPEC.isLoaded() || FAST_MATH.get();
    }

    public static boolean fastRandomEnabled() {
        return !SPEC.isLoaded() || FAST_RANDOM.get();
    }
}
