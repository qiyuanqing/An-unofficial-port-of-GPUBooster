package com.rapidyne;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RapidyneConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue FAST_ROTATE_SINGLE_AXIS = BUILDER
            .translation("rapidyne.config.fastRotateSingleAxis")
            .comment("Use Minecraft's lookup-table sin/cos for single-axis matrix rotations "
                    + "(rotateX/Y/Z, rotate around an arbitrary axis). Measured ~40% faster than "
                    + "vanilla JOML on the reference benchmark; results can vary by machine/JIT, "
                    + "so this can be turned off independently if it doesn't help on yours.")
            .define("fastRotateSingleAxis", true);

    public static final ModConfigSpec.BooleanValue FAST_ROTATE_COMBINED = BUILDER
            .translation("rapidyne.config.fastRotateCombined")
            .comment("Use Minecraft's lookup-table sin/cos for combined XYZ matrix rotations "
                    + "(rotateXYZ). Measured ~40% faster than vanilla JOML on the reference "
                    + "benchmark; results can vary by machine/JIT, so this can be turned off "
                    + "independently if it doesn't help on yours.")
            .define("fastRotateCombined", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    // Vanilla constructs PoseStack.Pose instances during very early static bootstrap
    // (e.g. biome registry init), before FML has finished loading this mod's config.
    // Fall back to the default (enabled) rather than let ModConfigSpec throw in that window.
    public static boolean fastRotateSingleAxisEnabled() {
        return !SPEC.isLoaded() || FAST_ROTATE_SINGLE_AXIS.get();
    }

    public static boolean fastRotateCombinedEnabled() {
        return !SPEC.isLoaded() || FAST_ROTATE_COMBINED.get();
    }
}
